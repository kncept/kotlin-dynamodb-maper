package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import com.kncept.mapper.collections.ListMapper
import kotlin.reflect.KClass

class ShortArrayMapper : TypeMapper<ShortArray> {
  val listMapper: ListMapper<Short> = ListMapper(ShortMapper())

  override fun type(): KClass<ShortArray> {
    return ShortArray::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return listMapper.attributeType()
  }

  override fun toItem(attribute: AttributeValue): ShortArray {
    return listMapper.toItem(attribute).toShortArray()
  }

  override fun toAttribute(item: ShortArray): AttributeValue {
    return listMapper.toAttribute(item.toList())
  }
}
