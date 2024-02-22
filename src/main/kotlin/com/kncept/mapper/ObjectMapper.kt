package com.kncept.mapper

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass

interface ObjectMapper {

  fun <T : Any> toItem(type: KClass<T>, attributes: Map<String, AttributeValue>): T

  fun <T : Any> toAttributes(item: T): Map<String, AttributeValue>
}
