package com.kncept.mapper.java.time

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

class ZonedDateTimeMapper : TypeMapper<ZonedDateTime> {
  override fun type(): KClass<ZonedDateTime> {
    return ZonedDateTime::class
  }

  val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toItem(attribute: AttributeValue): ZonedDateTime {
    return ZonedDateTime.parse(attribute.asS(), formatter)
  }

  override fun toAttribute(item: ZonedDateTime): AttributeValue {
    return AttributeValue.S(item.format(formatter))
  }
}
