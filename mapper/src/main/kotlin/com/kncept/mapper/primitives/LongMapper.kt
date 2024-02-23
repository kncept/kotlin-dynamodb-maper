package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class LongMapper : TypeMapper<Long> {
  override fun type(): KClass<Long> {
    return Long::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.N::class
  }

  override fun toType(attribute: AttributeValue): Long {
    return attribute.asN().toLong()
  }

  override fun toAttribute(item: Long): AttributeValue {
    return AttributeValue.N(item.toString())
  }
}
