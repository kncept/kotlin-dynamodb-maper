package com.kncept.mapper.primitive

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class StringMapper : TypeMapper<String> {
  override fun type(): KClass<String> {
    return String::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): String {
    return attribute.asS()
  }

  override fun toAttribute(item: String, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.S(item)
  }
}
