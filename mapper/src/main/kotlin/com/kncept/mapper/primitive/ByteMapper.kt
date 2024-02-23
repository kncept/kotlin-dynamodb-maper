package com.kncept.mapper.primitive

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class ByteMapper : TypeMapper<Byte> {
  override fun type(): KClass<Byte> {
    return Byte::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.N::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): Byte {
    return attribute.asN().toByte()
  }

  override fun toAttribute(item: Byte, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.N(item.toString())
  }
}
