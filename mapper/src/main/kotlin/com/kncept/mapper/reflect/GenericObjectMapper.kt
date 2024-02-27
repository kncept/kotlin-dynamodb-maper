package com.kncept.mapper.reflect

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.DynamoDbObjectMapper
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class GenericObjectMapper<T : Any>(
    private val mapper: DynamoDbObjectMapper,
    private val type: KClass<T>,
) : TypeMapper<T> {
  override fun type(): KClass<T> {
    return type
  }

  override fun toType(attribute: AttributeValue): T {
    val attributes: Map<String, AttributeValue> = attribute.asM()

    val creator = mapper.objectCreator(type)
    val args =
        creator
            .types()
            .map { constructorParam ->
              constructorParam.key to
                  attributes[constructorParam.key]?.let { attribute ->
                    try {
                      mapper.toItem(
                          constructorParam.value,
                          constructorParam.value.returnType.classifier as KClass<Any>,
                          attribute)
                    } catch (t: Throwable) {
                      throw IllegalStateException(
                          "error mapping ${type.simpleName} param ${constructorParam.key} to item from ${constructorParam.value.returnType.classifier}",
                          t)
                    }
                  }
            }
            .toMap()
    return creator.create(args) as T
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.M::class
  }

  override fun toAttribute(item: T): AttributeValue {
    val creator = mapper.objectCreator(item::class)
    val types = creator.types()
    val values = creator.values(item)
    val map =
        types
            .map { type ->
              try {
                mapper
                    .toAttributes(
                        type.value,
                        type.value.returnType.classifier as KClass<Any>,
                        values[type.key])
                    ?.let { type.key to it }
              } catch (t: Throwable) {
                throw IllegalStateException(
                    "error mapping ${item::class.simpleName} param ${type.key} to attribute from ${type.value.returnType.classifier}",
                    t)
              }
            }
            .filterNotNull()
            .toMap()

    return AttributeValue.M(map)
  }
}
