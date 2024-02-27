package com.kncept.mapper

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import kotlin.reflect.KClass

/**
 * The main interface to use.<br> After configuration, this should be enough for an app to fully map
 * and unmap data classes.
 */
interface ObjectMapper {

  /** Converts a dynamo db attributes map into an item */
  fun <T : Any> toItem(type: KClass<T>, attributes: Map<String, AttributeValue>): T

  /** Converts an item into an insertable dynamo db attributes map */
  fun <T : Any> toAttributes(item: T): Map<String, AttributeValue>
}
