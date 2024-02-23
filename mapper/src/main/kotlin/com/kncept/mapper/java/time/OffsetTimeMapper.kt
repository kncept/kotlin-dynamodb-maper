package com.kncept.mapper.java.time

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import java.time.OffsetTime
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

class OffsetTimeMapper : TypeMapper<OffsetTime> {
  override fun type(): KClass<OffsetTime> {
    return OffsetTime::class
  }

  val formatter = DateTimeFormatter.ISO_OFFSET_TIME

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toType(attribute: AttributeValue): OffsetTime {
    return OffsetTime.parse(attribute.asS(), formatter)
  }

  override fun toAttribute(item: OffsetTime): AttributeValue {
    return AttributeValue.S(item.format(formatter))
  }
}
