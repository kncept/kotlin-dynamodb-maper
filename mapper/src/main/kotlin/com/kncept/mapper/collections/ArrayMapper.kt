package com.kncept.mapper.collections

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class ArrayMapper<T : Any>(
    val collectionTypeMapper: TypeMapper<T>,
) : TypeMapper<Array<T>> {

  override fun type(): KClass<Array<T>> {
    return Array::class as KClass<Array<T>>
  }

  override fun toType(attribute: AttributeValue): Array<T> {
    val list = attribute.asL().map { collectionTypeMapper.toType(it) }
    val array =
        java.lang.reflect.Array.newInstance(collectionTypeMapper.type().java, list.size) as Array<T>
    list.forEachIndexed { index, item -> array[index] = item }
    return array
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.L::class
  }

  override fun toAttribute(item: Array<T>): AttributeValue {
    return AttributeValue.L(item.map { collectionTypeMapper.toAttribute(it) })
  }
}
