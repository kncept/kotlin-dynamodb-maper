package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class CharArrayMapper : TypeMapper<CharArray> {
  override fun type(): KClass<CharArray> {
    return CharArray::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toItem(attribute: AttributeValue): CharArray {
    return attribute.asS().toCharArray()
  }

  override fun toAttribute(item: CharArray): AttributeValue {
    return AttributeValue.S(item.concatToString())
  }
}
