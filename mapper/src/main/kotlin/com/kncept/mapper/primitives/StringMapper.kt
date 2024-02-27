package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class StringMapper : TypeMapper<String> {
  override fun type(): KClass<String> {
    return String::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toItem(attribute: AttributeValue): String {
    return attribute.asS()
  }

  override fun toAttribute(item: String): AttributeValue {
    return AttributeValue.S(item)
  }
}
