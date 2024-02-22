package com.kncept.mapper.java.time

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KClass

class ZonedDateTimeMapper : TypeMapper<ZonedDateTime> {
  override fun type(): KClass<ZonedDateTime> {
    return ZonedDateTime::class
  }

  val formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): ZonedDateTime {
    return ZonedDateTime.parse(attribute.asS(), formatter)
  }

  override fun toAttribute(item: ZonedDateTime, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.S(item.format(formatter))
  }
}
