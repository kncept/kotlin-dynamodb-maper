package com.kncept.mapper.reflect

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.DynamoDbObjectMapper
import com.kncept.mapper.TypeMapper
import java.lang.IllegalArgumentException
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

class MapMapper(
    private val mapper: DynamoDbObjectMapper,
    private val javaMathNumericTypes: Boolean = true
) : TypeMapper<Map<String, Any>> {
  override fun type(): KClass<Map<String, Any>> {
    return Map::class as KClass<Map<String, Any>>
  }

  override fun toType(attribute: AttributeValue): Map<String, Any> {
    return attribute
        .asM()
        .map { entry -> entry.key to unboundAttributeToProbableType(entry.value) }
        .toMap()
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.M::class
  }

  override fun toAttribute(item: Map<String, Any>): AttributeValue {
    return AttributeValue.M(
        item
            .map { entry ->
              mapper.asAttribute(entry.value::class, entry.value)?.let { value ->
                entry.key to value
              }
            }
            .filterNotNull()
            .toMap())
  }

  fun unboundAttributeToProbableType(value: AttributeValue): Any {
    return if (value is AttributeValue.M) toType(value)
    else if (value is AttributeValue.S) value.asS()
    else if (value is AttributeValue.N) {
      val n = value.asN()
      if (javaMathNumericTypes) {
        if (n.contains(".")) mapper.asItem(BigDecimal::class, value)
        else mapper.asItem(BigInteger::class, value)
      } else {
        if (n.contains(".")) mapper.asItem(Double::class, value)
        else mapper.asItem(Long::class, value)
      }
          as Any
    } else if (value is AttributeValue.Bool) value.asBool()
    else if (value is AttributeValue.B) value.asB()
    else throw IllegalArgumentException("Only simple maps are supported")
  }
}
