package com.kncept.mapper.primitive

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

class BooleanMapper : TypeMapper<Boolean> {
  override fun type(): KClass<Boolean> {
    return Boolean::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): Boolean {
    return attribute.asBool()
  }

  override fun toAttribute(item: Boolean, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.Bool(item)
  }
}
