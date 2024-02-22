package com.kncept.mapper.reflect

import kotlin.reflect.KClass

interface DataClassCreator<T : Any> {
  fun type(): KClass<T>

  fun constructorParams(): Map<String, KClass<Any>>

  fun values(item: Any): Map<String, Any?>

  fun create(args: Map<String, Any?>): T
}
