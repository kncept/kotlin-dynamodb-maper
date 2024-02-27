package com.kncept.mapper.primitives

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import com.kncept.mapper.collections.ListMapper
import kotlin.reflect.KClass

class FloatArrayMapper : TypeMapper<FloatArray> {
  val listMapper: ListMapper<Float> = ListMapper(FloatMapper())

  override fun type(): KClass<FloatArray> {
    return FloatArray::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return listMapper.attributeType()
  }

  override fun toType(attribute: AttributeValue): FloatArray {
    return listMapper.toType(attribute).toFloatArray()
  }

  override fun toAttribute(item: FloatArray): AttributeValue {
    return listMapper.toAttribute(item.toList())
  }
}
