package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class CharMapper : TypeMapper<Char> {
  override fun type(): KClass<Char> {
    return Char::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): Char {
    return attribute.asS().get(0)
  }

  override fun toAttribute(item: Char, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.S(item.toString())
  }
}
