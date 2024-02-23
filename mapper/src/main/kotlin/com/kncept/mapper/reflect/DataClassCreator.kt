package com.kncept.mapper.reflect

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

interface DataClassCreator<T : Any> {
  fun type(): KClass<T>

  fun types(): Map<String, KProperty<Any>>

  fun values(item: Any): Map<String, Any?>

  fun create(args: Map<String, Any?>): T
}
