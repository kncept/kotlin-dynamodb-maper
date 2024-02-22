package com.kncept.mapper

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.primitive.*
import com.kncept.mapper.reflect.DataClassCreator
import com.kncept.mapper.reflect.ReflectiveDataClassCreator
import kotlin.reflect.KClass

class DynamoDbObjectMapper : ObjectMapper {

  val objectCreators: MutableMap<KClass<out Any>, DataClassCreator<out Any>> = mutableMapOf()
  val typeMappers: MutableMap<KClass<out Any>, TypeMapper<out Any>> = mutableMapOf()

  init {
    TypeMapper.primitiveTypeMappers().forEach { register(it) }
    TypeMapper.javaMathTypeMappers().forEach { register(it) }
  }

  fun <T : Any> objectCreator(type: KClass<T>): DataClassCreator<out Any> {
    return objectCreators.getOrPut(type) { ReflectiveDataClassCreator(type) }
  }

  fun <T : Any> typeMapper(type: KClass<T>): TypeMapper<out Any> {
    return typeMappers[type] ?: throw NullPointerException("No mapper registered for type $type")
  }

  override fun <T : Any> toItem(type: KClass<T>, attributes: Map<String, AttributeValue>): T {
    val creator = objectCreator(type)
    val args =
        creator
            .constructorParams()
            .map { constructorParam ->
              constructorParam.key to
                  attributes[constructorParam.key]?.let { attribute ->
                    toItem(constructorParam.value, attribute)
                  }
            }
            .toMap()
    return creator.create(args) as T
  }

  override fun <T : Any> toAttributes(item: T): Map<String, AttributeValue> {
    val creator = objectCreator(item::class)

    return creator.values(item).map { it.key to toAttribute(it.value) }.toMap()
  }

  fun <T : Any> toAttribute(item: T?): AttributeValue {
    //        if () // omit nulls?
    if (item == null) return AttributeValue.Null(true)
    val mapper = typeMapper(item::class) as TypeMapper<Any>
    return mapper.toAttribute(item, this)
  }

  fun <T : Any> toItem(type: KClass<T>, attribute: AttributeValue): T? {
    if (attribute is AttributeValue.Null) return null
    val mapper = typeMapper(type)
    return mapper.toType(attribute, this) as T
  }

  fun register(mapper: TypeMapper<out Any>) {
    typeMappers[mapper.type()] = mapper
  }

  fun register(creator: DataClassCreator<out Any>) {
    objectCreators[creator.type()] = creator
  }
}
