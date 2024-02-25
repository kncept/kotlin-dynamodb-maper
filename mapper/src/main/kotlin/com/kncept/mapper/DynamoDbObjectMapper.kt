package com.kncept.mapper

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.annotation.MappedBy
import com.kncept.mapper.annotation.MappedCollection
import com.kncept.mapper.java.math.JavaMathModule
import com.kncept.mapper.java.time.JavaTimeModule
import com.kncept.mapper.java.util.JavaUtilModule
import com.kncept.mapper.primitives.PrimitivesModule
import com.kncept.mapper.reflect.*
import java.lang.IllegalStateException
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotations
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor

class DynamoDbObjectMapper(
    initialModules: List<TypeMapperModule> =
        listOf(
            PrimitivesModule(),
            JavaMathModule(),
            JavaTimeModule(),
            JavaUtilModule(),
        )
) : ObjectMapper {

  private val objectCreators: MutableMap<KClass<out Any>, DataClassCreator<out Any>> =
      mutableMapOf()
  private val typeMappers: MutableMap<KClass<out Any>, TypeMapper<out Any>> = mutableMapOf()
  private val mappedByCache: MutableMap<KClass<out Any>, TypeMapper<out Any>> = mutableMapOf()

  var emitNulls: Boolean = false
  var automapObjects: Boolean = true
  var mapEnumsByName: Boolean = true
  val javaMathNumericTypes: Boolean = false // unbox Maps to java.math or primitives

  // if there is any logic in contstruction, setting this to 'true' will (probably) cause it to
  // execute
  val filterNullArgsInConstruction: Boolean = false

  init {
    initialModules.forEach { register(it) }
  }

  fun <T : Any> objectCreator(type: KClass<T>): DataClassCreator<Any> {
    return objectCreators.getOrPut(type) {
      ReflectiveDataClassCreator(type, filterNullArgsInConstruction)
    } as DataClassCreator<Any>
  }

  fun typeMapper(type: KClass<out Any>): TypeMapper<Any> {
    val mapper = typeMappers[type] as TypeMapper<Any>?
    if (mapper == null && type.isSubclassOf(Enum::class)) {
      val newMapper = EnumMapper(type, mapEnumsByName)
      typeMappers[type] = newMapper
      return newMapper as TypeMapper<Any>
    }
    if (mapper == null && type.isSubclassOf(Map::class)) {
      val newMapper = MapMapper(this, javaMathNumericTypes)
      typeMappers[type] = newMapper
      return newMapper as TypeMapper<Any>
    }
    if (mapper == null && automapObjects) {
      if (type.declaredMemberProperties.isNotEmpty()) {
        val newMapper = GenericObjectMapper(this, type)
        typeMappers[type] = newMapper
        return newMapper as TypeMapper<Any>
      } else {
        throw IllegalStateException("Unable to create mapper for type $type")
      }
    }
    return mapper ?: throw IllegalStateException("Unable to lookup mapper for type $type")
  }

  fun mappedByTypeMapper(mappedBy: MappedBy): TypeMapper<Any> {
    return mappedByCache.getOrPut(mappedBy.typeMapper) {
      mappedBy.typeMapper.primaryConstructor!!.call()
    } as TypeMapper<Any>
  }

  override fun <T : Any> toItem(type: KClass<T>, attributes: Map<String, AttributeValue>): T {
    val creator = objectCreator(type)
    val args =
        creator
            .types()
            .map { constructorParam ->
              constructorParam.key to
                  attributes[constructorParam.key]?.let { attribute ->
                    asItem(constructorParam.value, attribute)
                  }
            }
            .toMap()
    return creator.create(args) as T
  }

  fun <T : Any> asItem(property: KProperty<T>, attribute: AttributeValue): T? {
    if (attribute is AttributeValue.Null) return null
    val type = property.returnType.classifier as KClass<out Any>

    val mappedBy = property.findAnnotations(MappedBy::class).firstOrNull()

    // handle an explicit MappedBy annotation on the property
    if (mappedBy != null) {
      val mapper = mappedByTypeMapper(mappedBy)
      return mapper.toType(attribute) as T?
    }

    if (type.isSubclassOf(Set::class)) {
      val annotation =
          property.findAnnotations(MappedCollection::class).firstOrNull()
              ?: throw IllegalStateException(
                  "Collections must specify their @MappedCollection type")
      when (typeMapper(annotation.componentType).attributeType()) {
        AttributeValue.S::class ->
            return attribute
                .asSs()
                .map { asItem(annotation.componentType, AttributeValue.S(it)) }
                .toSet() as T
        AttributeValue.N::class ->
            return attribute
                .asNs()
                .map { asItem(annotation.componentType, AttributeValue.N(it)) }
                .toSet() as T
        AttributeValue.B::class ->
            return attribute
                .asBs()
                .map { asItem(annotation.componentType, AttributeValue.B(it)) }
                .toSet() as T
        else ->
            throw IllegalStateException("Sets of type not supported: ${annotation.componentType}")
      }
    }
    if (type.isSubclassOf(List::class)) {
      val annotation =
          property.findAnnotations(MappedCollection::class).firstOrNull()
              ?: throw IllegalStateException(
                  "Collections must specify their @MappedCollection type")
      return attribute.asL().map { asItem(annotation.componentType, it) } as T
    }
    return asItem(property.returnType.classifier as KClass<T>, attribute)
  }

  fun <T : Any> asItem(type: KClass<T>, attribute: AttributeValue): T? {
    if (attribute is AttributeValue.Null) return null
    val mapper = typeMapper(type)
    return mapper.toType(attribute) as T
  }

  override fun <T : Any> toAttributes(item: T): Map<String, AttributeValue> {
    val creator = objectCreator(item::class)
    val types = creator.types()
    val values = creator.values(item)
    return types
        .map { type ->
          try {
            asAttribute(type.value, values[type.key])?.let { type.key to it }
          } catch (t: Throwable) {
            throw IllegalStateException(
                "error mapping ${item::class.simpleName} param ${type.key} ${type.value.returnType}",
                t)
          }
        }
        .filterNotNull()
        .toMap()
  }

  fun asAttribute(property: KProperty<out Any>, item: Any?): AttributeValue? {
    if (item == null && emitNulls) return AttributeValue.Null(true)
    if (item == null) return null
    val type = property.returnType.classifier as KClass<out Any>
    val mappedBy = property.findAnnotations(MappedBy::class).firstOrNull()

    // handle an explicit MappedBy annotation on the property
    if (mappedBy != null) {
      val mapper = mappedByTypeMapper(mappedBy)
      return mapper.toAttribute(item)
    }
    if (type.isSubclassOf(Set::class)) {
      val annotation =
          property.findAnnotations(MappedCollection::class).firstOrNull()
              ?: throw IllegalStateException(
                  "Collections must specify their @MappedCollection type")
      val list = (item as Set<*>).map { asAttribute(annotation.componentType, it) }.filterNotNull()
      when (typeMapper(annotation.componentType).attributeType()) {
        AttributeValue.S::class -> return AttributeValue.Ss(list.map { it.asS() })
        AttributeValue.N::class -> return AttributeValue.Ns(list.map { it.asN() })
        AttributeValue.B::class -> return AttributeValue.Bs(list.map { it.asB() })
        else ->
            throw IllegalStateException("Sets of type not supported: ${annotation.componentType}")
      }
    }
    if (type.isSubclassOf(List::class)) {
      val annotation =
          property.findAnnotations(MappedCollection::class).firstOrNull()
              ?: throw IllegalStateException(
                  "Collections must specify their @MappedCollection type")
      val list = (item as List<*>).map { asAttribute(annotation.componentType, it) }.filterNotNull()
      return AttributeValue.L(list)
    }
    return asAttribute(type, item)
  }

  fun asAttribute(type: KClass<out Any>, item: Any?): AttributeValue? {
    if (item == null && emitNulls) return AttributeValue.Null(true)
    if (item == null) return null

    // handle an explicit MappedBy annotation on the class
    val mappedBy = type.findAnnotations(MappedBy::class).firstOrNull()
    if (mappedBy != null) {
      val mapper = typeMapper(mappedBy.typeMapper)
      return mapper.toAttribute(item)
    }
    val mapper = typeMapper(type)
    return mapper.toAttribute(item)
  }

  fun register(module: TypeMapperModule) {
    module.mappers().forEach { register(it) }
  }

  fun register(mapper: TypeMapper<out Any>) {
    typeMappers[mapper.type()] = mapper
  }

  fun register(creator: DataClassCreator<out Any>) {
    objectCreators[creator.type()] = creator
  }
}
