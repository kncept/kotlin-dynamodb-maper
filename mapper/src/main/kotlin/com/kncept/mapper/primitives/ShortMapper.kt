package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class ShortMapper : TypeMapper<Short> {
  override fun type(): KClass<Short> {
    return Short::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.N::class
  }

  override fun toItem(attribute: AttributeValue): Short {
    return attribute.asN().toShort()
  }

  override fun toAttribute(item: Short): AttributeValue {
    return AttributeValue.N(item.toString())
  }
}
