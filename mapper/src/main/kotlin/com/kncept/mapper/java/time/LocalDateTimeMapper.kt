package com.kncept.mapper.java.time

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.reflect.KClass

class LocalDateTimeMapper : TypeMapper<LocalDateTime> {
  override fun type(): KClass<LocalDateTime> {
    return LocalDateTime::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): LocalDateTime {
    // epoch second
    if (attribute is AttributeValue.N) {
      return LocalDateTime.ofEpochSecond(attribute.asN().toLong(), 0, ZoneOffset.UTC)
    }
    return LocalDateTime.parse(attribute.asS())
  }

  override fun toAttribute(item: LocalDateTime, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.S(item.toString())
  }
}
