package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class IntMapper : TypeMapper<Int> {
  override fun type(): KClass<Int> {
    return Int::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.N::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): Int {
    return attribute.asN().toInt()
  }

  override fun toAttribute(item: Int, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.N(item.toString())
  }
}
