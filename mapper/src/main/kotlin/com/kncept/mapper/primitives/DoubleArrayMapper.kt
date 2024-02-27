package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import com.kncept.mapper.collections.ListMapper
import kotlin.reflect.KClass

class DoubleArrayMapper : TypeMapper<DoubleArray> {
  val listMapper: ListMapper<Double> = ListMapper(DoubleMapper())

  override fun type(): KClass<DoubleArray> {
    return DoubleArray::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return listMapper.attributeType()
  }

  override fun toItem(attribute: AttributeValue): DoubleArray {
    return listMapper.toItem(attribute).toDoubleArray()
  }

  override fun toAttribute(item: DoubleArray): AttributeValue {
    return listMapper.toAttribute(item.toList())
  }
}
