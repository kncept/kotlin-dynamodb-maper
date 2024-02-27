package com.kncept.mapper.java.util

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import java.util.UUID
import kotlin.reflect.KClass

class UUIDMapper : TypeMapper<UUID> {
  override fun type(): KClass<UUID> {
    return UUID::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toItem(attribute: AttributeValue): UUID {
    if (attribute is AttributeValue.B) return UUID.nameUUIDFromBytes(attribute.asB())
    return UUID.fromString(attribute.asS())
  }

  override fun toAttribute(item: UUID): AttributeValue {
    return AttributeValue.S(item.toString())
  }
}
