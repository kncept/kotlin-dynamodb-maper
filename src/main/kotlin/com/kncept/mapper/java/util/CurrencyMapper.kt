package com.kncept.mapper.java.util

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import java.util.*
import kotlin.reflect.KClass

class CurrencyMapper : TypeMapper<Currency> {
  override fun type(): KClass<Currency> {
    return Currency::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.S::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): Currency {
    return Currency.getInstance(attribute.asS())
  }

  override fun toAttribute(item: Currency, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.S(item.currencyCode)
  }
}
