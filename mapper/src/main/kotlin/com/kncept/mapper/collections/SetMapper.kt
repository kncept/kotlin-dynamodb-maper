package com.kncept.mapper.collections

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.TypeMapper
import java.lang.IllegalStateException
import kotlin.reflect.KClass

class SetMapper<T : Any>(
    val collectionTypeMapper: TypeMapper<T>,
) : TypeMapper<Set<T>> {

  override fun type(): KClass<Set<T>> {
    return Set::class as KClass<Set<T>>
  }

  override fun toType(attribute: AttributeValue): Set<T> {
    return when (collectionTypeMapper.attributeType()) {
      AttributeValue.S::class ->
          attribute
              .asSs()
              .map { AttributeValue.S(it) }
              .map { collectionTypeMapper.toType(it) }
              .toSet()
      AttributeValue.N::class ->
          attribute
              .asNs()
              .map { AttributeValue.N(it) }
              .map { collectionTypeMapper.toType(it) }
              .toSet()
      AttributeValue.B::class ->
          attribute
              .asBs()
              .map { AttributeValue.B(it) }
              .map { collectionTypeMapper.toType(it) }
              .toSet()
      else ->
          throw IllegalStateException(
              "Sets of type not supported: ${collectionTypeMapper.attributeType()}")
    }
  }

  override fun attributeType(): KClass<out AttributeValue> {
    return when (collectionTypeMapper.attributeType()) {
      AttributeValue.S::class -> AttributeValue.Ss::class
      else ->
          throw IllegalStateException(
              "Sets of type not supported: ${collectionTypeMapper.attributeType()}")
    }
  }

  override fun toAttribute(item: Set<T>): AttributeValue {
    val list = (item as Set<*>).map { collectionTypeMapper.toAttribute(it!! as T) }
    return when (collectionTypeMapper.attributeType()) {
      AttributeValue.S::class -> AttributeValue.Ss(list.map { it.asS() })
      AttributeValue.N::class -> AttributeValue.Ns(list.map { it.asN() })
      AttributeValue.B::class -> AttributeValue.Bs(list.map { it.asB() })
      else ->
          throw IllegalStateException(
              "Sets of type not supported: ${collectionTypeMapper.attributeType()}")
    }
  }
}
