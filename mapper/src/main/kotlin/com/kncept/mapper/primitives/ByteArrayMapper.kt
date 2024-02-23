package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class ByteArrayMapper : TypeMapper<ByteArray> {
  override fun type(): KClass<ByteArray> {
    return ByteArray::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.B::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): ByteArray {
    return attribute.asB()
  }

  override fun toAttribute(item: ByteArray, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.B(item)
  }
}
