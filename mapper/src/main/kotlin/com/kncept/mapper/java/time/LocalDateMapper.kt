package com.kncept.mapper.java.time

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

class LocalDateMapper : TypeMapper<LocalDate> {
  override fun type(): KClass<LocalDate> {
    return LocalDate::class
  }

  val formatter = DateTimeFormatter.ISO_DATE

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toItem(attribute: AttributeValue): LocalDate {
    return LocalDate.parse(attribute.asS(), formatter)
  }

  override fun toAttribute(item: LocalDate): AttributeValue {
    return AttributeValue.S(item.format(formatter))
  }
}
