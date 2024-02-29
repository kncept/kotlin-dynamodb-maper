package com.kncept.mapper.collections

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class ListMapper<T : Any>(
    val collectionTypeMapper: TypeMapper<T>,
) : TypeMapper<List<T?>> {

  override fun type(): KClass<List<T?>> {
    return List::class as KClass<List<T?>>
  }

  override fun toItem(attribute: AttributeValue): List<T?> {
    return attribute.asL().map {
      if (it is AttributeValue.Null) null else collectionTypeMapper.toItem(it)
    }
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.L::class
  }

  override fun toAttribute(item: List<T?>): AttributeValue {
    return AttributeValue.L(
        item.map {
          if (it == null) AttributeValue.Null(true) else collectionTypeMapper.toAttribute(it)
        })
  }
}
