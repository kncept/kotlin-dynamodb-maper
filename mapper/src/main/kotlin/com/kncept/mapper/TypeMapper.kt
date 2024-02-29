package com.kncept.mapper

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass

/** Interface for converting a specific type<br> */
interface TypeMapper<T : Any> {

  /** The Kotlin Class type that this TypeMapper is for<br> */
  fun type(): KClass<T>

  /** Convert from an attribute to an item<br> */
  fun toItem(attribute: AttributeValue): T?

  /** Convert from an item to an attribute<br> */
  fun toAttribute(item: T): AttributeValue

  /** The output AttributeValue type from this class<br> */
  fun attributeType(): KClass<out AttributeValue>
}
