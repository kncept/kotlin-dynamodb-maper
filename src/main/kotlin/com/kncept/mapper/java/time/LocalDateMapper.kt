package com.kncept.mapper.java.time

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.reflect.KClass

class LocalDateMapper : TypeMapper<LocalDate> {
  override fun type(): KClass<LocalDate> {
    return LocalDate::class
  }

  val formatter = DateTimeFormatter.ISO_DATE

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): LocalDate {
    return LocalDate.parse(attribute.asS(), formatter)
  }

  override fun toAttribute(item: LocalDate, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.S(item.format(formatter))
  }
}
