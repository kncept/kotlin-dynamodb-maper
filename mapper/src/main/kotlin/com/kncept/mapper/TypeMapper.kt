package com.kncept.mapper

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass

interface TypeMapper<T : Any> {

  /** The Kotlin Class type that this TypeMapper is for */
  fun type(): KClass<T>

  /**
   * Convert from an attribute to an object<br>
   *
   * the mapper is included to allow for easy nested mapping support
   */
  fun toType(attribute: AttributeValue): T

  /**
   * Convert from an object to an attribute<br>
   *
   * the mapper is included to allow for easy nested mapping support
   */
  fun toAttribute(item: T): AttributeValue

  fun attributeType(): KClass<out AttributeValue>
}
