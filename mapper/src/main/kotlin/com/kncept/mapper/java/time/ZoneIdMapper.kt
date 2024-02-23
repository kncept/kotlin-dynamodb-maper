package com.kncept.mapper.java.time

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import java.time.ZoneId
import kotlin.reflect.KClass

class ZoneIdMapper : TypeMapper<ZoneId> {
  override fun type(): KClass<ZoneId> {
    return ZoneId::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): ZoneId {
    return ZoneId.of(attribute.asS())
  }

  override fun toAttribute(item: ZoneId, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.S(item.toString())
  }
}
