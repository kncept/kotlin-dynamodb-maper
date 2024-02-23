package com.kncept.mapper.primitive

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class FloatMapper : TypeMapper<Float> {
  override fun type(): KClass<Float> {
    return Float::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.N::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): Float {
    return attribute.asN().toFloat()
  }

  override fun toAttribute(item: Float, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.N(item.toString())
  }
}
