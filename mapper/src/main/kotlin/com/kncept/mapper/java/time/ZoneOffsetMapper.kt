package com.kncept.mapper.java.time

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import java.time.ZoneOffset
import kotlin.reflect.KClass

class ZoneOffsetMapper : TypeMapper<ZoneOffset> {
  override fun type(): KClass<ZoneOffset> {
    return ZoneOffset::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): ZoneOffset {
    return ZoneOffset.of(attribute.asS())
  }

  override fun toAttribute(item: ZoneOffset, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.S(item.toString())
  }
}
