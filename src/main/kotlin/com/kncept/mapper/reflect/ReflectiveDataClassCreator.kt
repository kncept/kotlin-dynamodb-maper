package com.kncept.mapper.reflect

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

class ReflectiveDataClassCreator<T : Any>(private val type: KClass<T>) : DataClassCreator<T> {

  override fun type(): KClass<T> {
    return type
  }

  override fun constructorParams(): Map<String, KClass<Any>> {
    return type.primaryConstructor!!
        .parameters
        .map { it.name!! to it.type.classifier as KClass<Any> }
        .toMap()
  }

  override fun values(item: Any): Map<String, Any?> {
    return type.declaredMemberProperties.map { it.name to it.getter.call(item) }.toMap()
  }

  override fun create(args: Map<String, Any?>): T {
    val mappedArgs = type.primaryConstructor!!.parameters.map { it to args[it.name] }.toMap()
    return type.primaryConstructor!!.callBy(mappedArgs)
  }
}
