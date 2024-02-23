package com.kncept.mapper.java.math

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
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

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): BigDecimal {
    if (attribute is AttributeValue.S) return BigDecimal(attribute.asS())
    return BigDecimal(attribute.asN())
  }

  override fun toAttribute(item: BigDecimal, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.N(item.toPlainString())
  }
}
