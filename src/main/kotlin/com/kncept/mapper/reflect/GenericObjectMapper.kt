package com.kncept.mapper.reflect

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class GenericObjectMapper<T : Any>(private val type: KClass<T>) : TypeMapper<T> {
  override fun type(): KClass<T> {
    return type
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): T {
    return mapper.toItem(type, attribute.asM())
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.M::class
  }

  override fun toAttribute(item: T, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.M(mapper.toAttributes(item))
  }
}
