package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import com.kncept.mapper.collections.ListMapper
import kotlin.reflect.KClass

class LongArrayMapper : TypeMapper<LongArray> {
  val listMapper: ListMapper<Long> = ListMapper(LongMapper())

  override fun type(): KClass<LongArray> {
    return LongArray::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return listMapper.attributeType()
  }

  override fun toItem(attribute: AttributeValue): LongArray {
    return listMapper.toItem(attribute).toLongArray()
  }

  override fun toAttribute(item: LongArray): AttributeValue {
    return listMapper.toAttribute(item.toList())
  }
}
