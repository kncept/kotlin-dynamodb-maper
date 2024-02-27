package com.kncept.mapper.java.time

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

class LocalTimeMapper : TypeMapper<LocalTime> {
  override fun type(): KClass<LocalTime> {
    return LocalTime::class
  }

  val formatter = DateTimeFormatter.ISO_TIME

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toItem(attribute: AttributeValue): LocalTime {
    return LocalTime.parse(attribute.asS(), formatter)
  }

  override fun toAttribute(item: LocalTime): AttributeValue {
    return AttributeValue.S(item.format(formatter))
  }
}
