package com.kncept.mapper.java.math

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.ObjectMapper
import com.kncept.mapper.TypeMapper
import java.math.BigInteger
import kotlin.reflect.KClass

class BigIntegerMapper : TypeMapper<BigInteger> {
  override fun type(): KClass<BigInteger> {
    return BigInteger::class
  }

  override fun toType(attribute: AttributeValue, mapper: ObjectMapper): BigInteger {
    return BigInteger(attribute.asN())
  }

  override fun toAttribute(item: BigInteger, mapper: ObjectMapper): AttributeValue {
    return AttributeValue.N(item.toString())
  }
}
