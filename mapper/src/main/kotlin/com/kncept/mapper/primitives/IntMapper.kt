package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class IntMapper : TypeMapper<Int> {
  override fun type(): KClass<Int> {
    return Int::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.N::class
  }

  override fun toItem(attribute: AttributeValue): Int {
    return attribute.asN().toInt()
  }

  override fun toAttribute(item: Int): AttributeValue {
    return AttributeValue.N(item.toString())
  }
}
