package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class BooleanArrayMapper : TypeMapper<BooleanArray> {
  override fun type(): KClass<BooleanArray> {
    return BooleanArray::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.B::class
  }

  override fun toItem(attribute: AttributeValue): BooleanArray {
    return attribute.asB().map { it.toInt() == 1 }.toBooleanArray()
  }

  override fun toAttribute(item: BooleanArray): AttributeValue {
    val bytes = item.map { if (it) 1 else 0 }.map { it.toByte() }.toByteArray()
    return AttributeValue.B(bytes)
  }
}
