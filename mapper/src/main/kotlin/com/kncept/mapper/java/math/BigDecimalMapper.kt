package com.kncept.mapper.java.math

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import java.math.BigDecimal
import kotlin.reflect.KClass

class BigDecimalMapper : TypeMapper<BigDecimal> {
  override fun type(): KClass<BigDecimal> {
    return BigDecimal::class
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return AttributeValue.N::class
  }

  override fun toItem(attribute: AttributeValue): BigDecimal {
    if (attribute is AttributeValue.S) return BigDecimal(attribute.asS())
    return BigDecimal(attribute.asN())
  }

  override fun toAttribute(item: BigDecimal): AttributeValue {
    return AttributeValue.N(item.toPlainString())
  }
}
