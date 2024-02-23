package com.kncept.mapper.java.time

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import java.time.Instant
import kotlin.reflect.KClass

class InstantMapper(val truncateTypesToEpochSecond: Boolean) : TypeMapper<Instant> {
  override fun type(): KClass<Instant> {
    return Instant::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.N::class
  }

  override fun toType(attribute: AttributeValue): Instant {
    if (truncateTypesToEpochSecond) return Instant.ofEpochSecond(attribute.asN().toLong())
    return Instant.parse(attribute.asN())
  }

  override fun toAttribute(item: Instant): AttributeValue {
    if (truncateTypesToEpochSecond) return AttributeValue.N(item.epochSecond.toString())
    return AttributeValue.N(item.toString())
  }
}
