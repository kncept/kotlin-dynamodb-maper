package com.kncept.mapper

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.annotation.MappedBy
import com.kncept.mapper.annotation.MappedCollection
import com.kncept.mapper.collections.ListMapper
import com.kncept.mapper.collections.MapMapper
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
  // execute.
  // eg: can fail RecursiveTypes() test
  val filterNullArgsInConstruction: Boolean = false

  init {
    initialModules.forEach { register(it) }
  }

  fun <T : Any> objectCreator(type: KClass<T>): DataClassCreator<Any> {
    return objectCreators.getOrPut(type) {
      ReflectiveDataClassCreator(type, filterNullArgsInConstruction)
    } as DataClassCreator<Any>
  }

  fun typeMapper(type: KClass<out Any>): TypeMapper<Any>? {
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
    if (mapper == null &&
        automapObjects &&
        type != Set::class &&
        type != List::class &&
        type != Array::class) {
      if (type.declaredMemberProperties.isNotEmpty()) {
        val newMapper = GenericObjectMapper(this, type)
        typeMappers[type] = newMapper
        return newMapper as TypeMapper<Any>
      } else {
        throw IllegalStateException("Unable to create mapper for type $type")
      }
    }
    return mapper
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

    val mappedBy = property.findAnnotations(MappedBy::class).firstOrNull()
    // handle an explicit MappedBy annotation on the property
    if (mappedBy != null) {
      val mapper = mappedByTypeMapper(mappedBy)
      return mapper.toType(attribute) as T?
    }

    val type = property.returnType.classifier as KClass<out Any>
    val mapper = typeMapper(type)
    if (mapper != null) return mapper.toType(attribute) as T

    if (type.isSubclassOf(Set::class)) {
      val annotation =
          property.findAnnotations(MappedCollection::class).firstOrNull()
              ?: throw IllegalStateException(
                  "Collections must specify their @MappedCollection type")
      typeMapper(annotation.componentType)?.let { collectionTypeMapper ->
        when (collectionTypeMapper.attributeType()) {
          AttributeValue.S::class ->
              return attribute
                  .asSs()
                  .map { AttributeValue.S(it) }
                  .map { collectionTypeMapper.toType(it) }
                  .toSet() as T
          AttributeValue.N::class ->
              return attribute
                  .asNs()
                  .map { AttributeValue.N(it) }
                  .map { collectionTypeMapper.toType(it) }
                  .toSet() as T
          AttributeValue.B::class ->
              return attribute
                  .asBs()
                  .map { AttributeValue.B(it) }
                  .map { collectionTypeMapper.toType(it) }
                  .toSet() as T
          else ->
              throw IllegalStateException("Sets of type not supported: ${annotation.componentType}")
        }
      }
    }
    if (type.isSubclassOf(List::class)) {
      val annotation =
          property.findAnnotations(MappedCollection::class).firstOrNull()
              ?: throw IllegalStateException(
                  "Collections must specify their @MappedCollection type")
      typeMapper(annotation.componentType)?.let { collectionTypeMapper ->
        return ListMapper(collectionTypeMapper).toType(attribute) as T
      }
    }
    throw IllegalStateException("Unable to map type $type")
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

    val mappedBy = property.findAnnotations(MappedBy::class).firstOrNull()
    // handle an explicit MappedBy annotation on the property
    if (mappedBy != null) {
      val mapper = mappedByTypeMapper(mappedBy)
      return mapper.toAttribute(item)
    }

    val type = property.returnType.classifier as KClass<out Any>
    val mapper = typeMapper(type)
    if (mapper != null) return mapper.toAttribute(item)

    if (type.isSubclassOf(Set::class)) {
      val annotation =
          property.findAnnotations(MappedCollection::class).firstOrNull()
              ?: throw IllegalStateException(
                  "Collections must specify their @MappedCollection type")
      typeMapper(annotation.componentType)?.let { collectionTypeMapper ->
        val list = (item as Set<*>).map { collectionTypeMapper.toAttribute(it!!) }
        when (collectionTypeMapper.attributeType()) {
          AttributeValue.S::class -> return AttributeValue.Ss(list.map { it.asS() })
          AttributeValue.N::class -> return AttributeValue.Ns(list.map { it.asN() })
          AttributeValue.B::class -> return AttributeValue.Bs(list.map { it.asB() })
          else ->
              throw IllegalStateException("Sets of type not supported: ${annotation.componentType}")
        }
      }
    }
    if (type.isSubclassOf(List::class)) {
      val annotation =
          property.findAnnotations(MappedCollection::class).firstOrNull()
              ?: throw IllegalStateException(
                  "Collections must specify their @MappedCollection type")
      typeMapper(annotation.componentType)?.let { collectionTypeMapper ->
        return ListMapper(collectionTypeMapper).toAttribute(item as List<Any>)
      }
    }

    throw IllegalStateException("Unable to map type $type")
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
