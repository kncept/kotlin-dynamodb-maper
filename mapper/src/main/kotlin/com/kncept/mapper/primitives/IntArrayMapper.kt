package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import com.kncept.mapper.collections.ListMapper
import kotlin.reflect.KClass

class IntArrayMapper : TypeMapper<IntArray> {
  val listMapper: ListMapper<Int> = ListMapper(IntMapper())

  override fun type(): KClass<IntArray> {
    return IntArray::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return listMapper.attributeType()
  }

  override fun toItem(attribute: AttributeValue): IntArray {
    return listMapper.toItem(attribute).toIntArray()
  }

  override fun toAttribute(item: IntArray): AttributeValue {
    return listMapper.toAttribute(item.toList())
  }
}
