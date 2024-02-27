package com.kncept.mapper.java.time

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

class OffsetDateTimeMapper : TypeMapper<OffsetDateTime> {
  override fun type(): KClass<OffsetDateTime> {
    return OffsetDateTime::class
  }

  val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toItem(attribute: AttributeValue): OffsetDateTime {
    return OffsetDateTime.parse(attribute.asS(), formatter)
  }

  override fun toAttribute(item: OffsetDateTime): AttributeValue {
    return AttributeValue.S(item.format(formatter))
  }
}
