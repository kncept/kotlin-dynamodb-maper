package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class FloatMapper : TypeMapper<Float> {
  override fun type(): KClass<Float> {
    return Float::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.N::class
  }

  override fun toItem(attribute: AttributeValue): Float {
    return attribute.asN().toFloat()
  }

  override fun toAttribute(item: Float): AttributeValue {
    return AttributeValue.N(item.toString())
  }
}
