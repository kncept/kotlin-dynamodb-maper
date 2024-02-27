package com.kncept.mapper.java.util

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import java.util.*
import kotlin.reflect.KClass

class LocaleMapper : TypeMapper<Locale> {
  override fun type(): KClass<Locale> {
    return Locale::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toItem(attribute: AttributeValue): Locale {
    return Locale.forLanguageTag(attribute.asS())
  }

  override fun toAttribute(item: Locale): AttributeValue {
    return AttributeValue.S(item.toLanguageTag())
  }
}
