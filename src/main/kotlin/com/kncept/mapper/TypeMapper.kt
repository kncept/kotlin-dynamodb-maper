package com.kncept.mapper

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.java.math.BigDecimalMapper
import com.kncept.mapper.java.math.BigIntegerMapper
import com.kncept.mapper.java.util.UUIDMapper
import com.kncept.mapper.primitive.*
import kotlin.reflect.KClass

interface TypeMapper<T : Any> {

  /** The Kotlin Class type that this TypeMapper is for */
  fun type(): KClass<T>

  /**
   * Convert from an attribute to an object<br>
   *
   * the mapper is included to allow for easy nested mapping support
   */
  fun toType(attribute: AttributeValue, mapper: ObjectMapper): T

  /**
   * Convert from an object to an attribute<br>
   *
   * the mapper is included to allow for easy nested mapping support
   */
  fun toAttribute(item: T, mapper: ObjectMapper): AttributeValue

  fun attributeType(): KClass<out AttributeValue>

  companion object {

    fun primitiveTypeMappers(): List<TypeMapper<*>> {
      return listOf(
          ByteArrayMapper(),
          BooleanMapper(),
          ByteMapper(),
          ShortMapper(),
          IntMapper(),
          LongMapper(),
          FloatMapper(),
          DoubleMapper(),
          CharMapper(),
          StringMapper(),
      )
    }

    fun javaMathTypeMappers(): List<TypeMapper<*>> {
      return listOf(
          BigIntegerMapper(),
          BigDecimalMapper(),
      )
    }

    fun javaUtilTypeMappers(): List<TypeMapper<*>> {
      return listOf(UUIDMapper())
    }
  }
}
