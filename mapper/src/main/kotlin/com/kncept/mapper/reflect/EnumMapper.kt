package com.kncept.mapper.reflect

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.memberProperties

class EnumMapper<T : Any>(
    private val type: KClass<T>,
    private val byName: Boolean,
) : TypeMapper<T> {
  override fun type(): KClass<T> {
    return type
  }

  val valueOfFunction = type.declaredFunctions.firstOrNull { it.name == "valueOf" }!!
  val valuesFunction = type.declaredFunctions.firstOrNull { it.name == "values" }!!
  val nameProperty = type.memberProperties.firstOrNull { it.name == "name" }!!
  val ordinalProperty = type.memberProperties.firstOrNull { it.name == "ordinal" }!!

  override fun toType(attribute: AttributeValue): T {
    if (attribute is AttributeValue.N) {
      val values: Array<T> = valuesFunction.call() as Array<T>
      val ordinal = attribute.asN().toInt()
      return values[ordinal]
    }
    return valueOfFunction.call(attribute.asS()) as T
  }

  override fun attributeType(): KClass<out AttributeValue> {
    if (byName) return AttributeValue.S::class
    return AttributeValue.N::class
  }

  override fun toAttribute(item: T): AttributeValue {
    if (byName) return AttributeValue.S(nameProperty.get(item) as String)
    return AttributeValue.N(ordinalProperty.get(item).toString())
  }
}
