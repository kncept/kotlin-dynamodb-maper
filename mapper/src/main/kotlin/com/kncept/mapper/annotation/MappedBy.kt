package com.kncept.mapper.annotation

import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class MappedBy(val typeMapper: KClass<out TypeMapper<out Any>>)
