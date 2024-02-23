package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class ByteMapper : TypeMapper<Byte> {
  override fun type(): KClass<Byte> {
    return Byte::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.N::class
  }

  override fun toType(attribute: AttributeValue): Byte {
    return attribute.asN().toByte()
  }

  override fun toAttribute(item: Byte): AttributeValue {
    return AttributeValue.N(item.toString())
  }
}
