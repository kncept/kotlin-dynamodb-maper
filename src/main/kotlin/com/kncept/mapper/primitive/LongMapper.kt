package com.kncept.mapper.primitive

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class LongMapper : TypeMapper<Long> {
  override fun type(): KClass<Long> {
    return Long::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.N::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): Long {
    return attribute.asN().toLong()
  }

  override fun toAttribute(item: Long, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.N(item.toString())
  }
}
