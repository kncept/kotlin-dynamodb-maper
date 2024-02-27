package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class DoubleMapper : TypeMapper<Double> {
  override fun type(): KClass<Double> {
    return Double::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.N::class
  }

  override fun toItem(attribute: AttributeValue): Double {
    return attribute.asN().toDouble()
  }

  override fun toAttribute(item: Double): AttributeValue {
    return AttributeValue.N(item.toString())
  }
}
