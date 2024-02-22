package com.kncept.mapper.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
annotation class MappedCollection(val componentType: KClass<out Any>)
