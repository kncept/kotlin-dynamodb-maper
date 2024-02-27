package com.kncept.mapper.java.time

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import java.time.Duration
import kotlin.reflect.KClass

class DurationMapper : TypeMapper<Duration> {
  override fun type(): KClass<Duration> {
    return Duration::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toItem(attribute: AttributeValue): Duration {
    return Duration.parse(attribute.asS())
  }

  override fun toAttribute(item: Duration): AttributeValue {
    return AttributeValue.S(item.toString())
  }
}
