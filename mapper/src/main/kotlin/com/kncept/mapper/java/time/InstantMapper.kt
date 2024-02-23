package com.kncept.mapper.java.time

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import java.time.Instant
import kotlin.reflect.KClass

class InstantMapper : TypeMapper<Instant> {
  override fun type(): KClass<Instant> {
    return Instant::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.N::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): Instant {
    return Instant.ofEpochSecond(attribute.asN().toLong())
  }

  override fun toAttribute(item: Instant, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.N(item.epochSecond.toString())
  }
}
