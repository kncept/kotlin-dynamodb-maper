package com.kncept.mapper

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.annotation.MappedBy
import com.kncept.mapper.annotation.MappedCollection
import com.kncept.mapper.collections.ArrayMapper
import com.kncept.mapper.collections.ListMapper
import com.kncept.mapper.collections.MapMapper
import com.kncept.mapper.collections.SetMapper
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

/**
 * Main entry point for API usage<br>
 *
 * Create, Customise and Use a DynamoDbObjectMapper()<br>
 *
 * Note that you can override the initial, default set of modules. This is not recommended, but if
 * you really really REALLY need to, you can. Though I'd strongly recommend including at least the
 * PrimitivesModule() in whatever else you are doing.
 */
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

  /**
   * Config: emitNulls<br> controls emitting 'null = true' attributes, or emitting nothing at all
   */
  var emitNulls: Boolean = false

  /** Config: mapEnumsByName<br> controls emitting enums by name or by ordinal index */
  var mapEnumsByName: Boolean = true

  /**
   * Config: javaMathNumericTypes<br> controls unboxing generic 'number' types to Long/Double, or
   * BigInteger/BigDecimal
   */
  val javaMathNumericTypes: Boolean = true // unbox Maps to java.math or primitives

  /**
   * Config: filterNullArgsInConstruction<br> controls if null values are pre-filtered out of a
   * constructor<br> WARNING: If you have any constructor logic for defaults, this setting will
   * affect it's behaviour.<br>
   */
  // Play with the RecursiveTypes() test to see how this can effect things
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
    return mapper
  }

  fun mappedByTypeMapper(property: KProperty<Any>?, type: KClass<Any>): TypeMapper<Any>? {
    val mappedBy =
        property?.findAnnotations(MappedBy::class)?.firstOrNull()
            ?: type.findAnnotations(MappedBy::class).firstOrNull()
    if (mappedBy != null) {
      return mappedByCache.getOrPut(mappedBy.typeMapper) {
        mappedBy.typeMapper.primaryConstructor!!.call()
      } as TypeMapper<Any>
    }
    return null
  }

  // note that this will NOT return true for kotlin builtin primitive arrays, eg: ByteArray
  fun isCollectionType(type: KClass<out Any>): Boolean {
    // filter out kotlin builtins, we have primitive mappings for those
    if (type == BooleanArray::class) return false
    if (type == ByteArray::class) return false
    if (type == CharArray::class) return false
    if (type == DoubleArray::class) return false
    if (type == FloatArray::class) return false
    if (type == LongArray::class) return false
    if (type == IntArray::class) return false
    if (type == ShortArray::class) return false

    if (type.isSubclassOf(Set::class)) return true
    if (type.isSubclassOf(List::class)) return true
    if (type.isSubclassOf(Array::class)) return true
    if (type.java.isArray) return true // ugh

    if (type.isSubclassOf(Map::class)) return true

    return false
  }

  fun collectionComponentType(property: KProperty<Any>): KClass<out Any> {
    val annotation = property?.findAnnotations(MappedCollection::class)?.firstOrNull()
    if (annotation != null) return annotation.componentType

    val genericArgs = property?.returnType?.arguments
    if (!genericArgs.isNullOrEmpty()) {
      val componentType = genericArgs[0].type?.classifier
      if (componentType != null) return componentType as KClass<out Any>
    }
    throw IllegalStateException(
        "Unable to determine collection type, please use a @MappedCollection annotation")
  }

  override fun <T : Any> toItem(type: KClass<T>, attributes: Map<String, AttributeValue>): T {
    return toItem(null, type, AttributeValue.M(attributes)) as T
  }

  fun <T : Any> toItem(property: KProperty<T>?, type: KClass<T>, attribute: AttributeValue): T? {
    if (attribute is AttributeValue.Null) return null

    val mappedByMapper = mappedByTypeMapper(property, type as KClass<Any>)
    if (mappedByMapper != null) return mappedByMapper.toItem(attribute) as T

    if (isCollectionType(type)) {
      if (type.isSubclassOf(Set::class)) {
        val componentType = collectionComponentType(property as KProperty<Any>)
        typeMapper(componentType)?.let { collectionTypeMapper ->
          return SetMapper(collectionTypeMapper).toItem(attribute) as T
        }
      }
      if (type.isSubclassOf(List::class)) {
        val componentType = collectionComponentType(property as KProperty<Any>)
        typeMapper(componentType)?.let { collectionTypeMapper ->
          return ListMapper(collectionTypeMapper).toItem(attribute) as T
        }
      }
      if (type.java.isArray) { // type.isSubclassOf(Array::class) << doesn't work :/
        val componentType = collectionComponentType(property as KProperty<Any>)
        typeMapper(componentType)?.let { collectionTypeMapper ->
          return ArrayMapper(collectionTypeMapper).toItem(attribute) as T
        }
      }
      if (type.isSubclassOf(Map::class)) {
        return MapMapper(this, javaMathNumericTypes).toItem(attribute) as T
      }
      throw IllegalStateException("unable to map to item collection of type $type")
    }

    val mapper = typeMapper(type)
    if (mapper != null) return mapper.toItem(attribute) as T

    if (type.declaredMemberProperties.isNotEmpty()) {
      val newMapper = GenericObjectMapper(this, type)
      typeMappers[type] = newMapper
      return newMapper.toItem(attribute) as T
    }

    throw IllegalStateException("Unable to convert attribute to item: $type")
  }

  override fun <T : Any> toAttributes(item: T): Map<String, AttributeValue> {
    return toAttributes(null, item::class, item)!!.asM()
  }

  fun <T : Any> toAttributes(
      property: KProperty<T>?,
      type: KClass<T>,
      item: Any?
  ): AttributeValue? {
    if (item == null && emitNulls) return AttributeValue.Null(true)
    if (item == null) return null

    val mappedByMapper = mappedByTypeMapper(property, type as KClass<Any>)
    if (mappedByMapper != null) return mappedByMapper.toAttribute(item)

    if (isCollectionType(type)) {
      if (type.isSubclassOf(Set::class)) {
        val componentType = collectionComponentType(property as KProperty<Any>)
        typeMapper(componentType)?.let { collectionTypeMapper ->
          return SetMapper(collectionTypeMapper).toAttribute(item as Set<Any?>)
        }
      }
      if (type.isSubclassOf(List::class)) {
        val componentType = collectionComponentType(property as KProperty<Any>)
        typeMapper(componentType)?.let { collectionTypeMapper ->
          return ListMapper(collectionTypeMapper).toAttribute(item as List<Any?>)
        }
      }
      if (type.java.isArray) { // type.isSubclassOf(Array::class) << doesn't work :/
        val componentType = collectionComponentType(property as KProperty<Any>)
        typeMapper(componentType)?.let { collectionTypeMapper ->
          return ArrayMapper(collectionTypeMapper).toAttribute(item as Array<Any?>)
        }
      }
      if (type.isSubclassOf(Map::class)) {
        return MapMapper(this, javaMathNumericTypes).toAttribute(item as Map<String, Any>)
      }

      throw IllegalStateException("unable to map to attribute collection of type $type")
    }

    val mapper = typeMapper(type)
    if (mapper != null) return mapper.toAttribute(item)

    if (type.declaredMemberProperties.isNotEmpty()) {
      val newMapper = GenericObjectMapper(this, type) as TypeMapper<Any>
      typeMappers[type] = newMapper
      return newMapper.toAttribute(item)
    }

    throw IllegalStateException("Unable to convert item to attribute $type")
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
