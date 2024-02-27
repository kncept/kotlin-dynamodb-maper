package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class BooleanMapper : TypeMapper<Boolean> {
  override fun type(): KClass<Boolean> {
    return Boolean::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.Bool::class
  }

  override fun toItem(attribute: AttributeValue): Boolean {
    if (attribute is AttributeValue.S) return attribute.asS().toBoolean()
    return attribute.asBool()
  }

  override fun toAttribute(item: Boolean): AttributeValue {
    return AttributeValue.Bool(item)
  }
}
