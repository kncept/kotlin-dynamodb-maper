package com.kncept.mapper.java.util

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

class DateMapper : TypeMapper<Date> {
  override fun type(): KClass<Date> {
    return Date::class
  }

  val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): Date {
    return formatter.parse(attribute.asS())
  }

  override fun toAttribute(item: Date, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.S(formatter.format(item))
  }
}
