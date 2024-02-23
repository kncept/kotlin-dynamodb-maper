package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class ShortMapper : TypeMapper<Short> {
  override fun type(): KClass<Short> {
    return Short::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.N::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): Short {
    return attribute.asN().toShort()
  }

  override fun toAttribute(item: Short, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.N(item.toString())
  }
}
