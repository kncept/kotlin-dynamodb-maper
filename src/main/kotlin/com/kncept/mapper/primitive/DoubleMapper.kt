package com.kncept.mapper.primitive

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class DoubleMapper : TypeMapper<Double> {
  override fun type(): KClass<Double> {
    return Double::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): Double {
    return attribute.asN().toDouble()
  }

  override fun toAttribute(item: Double, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.N(item.toString())
  }
}
