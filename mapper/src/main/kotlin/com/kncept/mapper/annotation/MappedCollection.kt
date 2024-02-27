package com.kncept.mapper.annotation

import kotlin.reflect.KClass

/**
 * Annotation to determine the contents of a collection.<br> This has priority over any detected
 * collection types.<br>
 *
 * If detection fails for any reason, use this annotation to force it.<br>
 */
@Target(AnnotationTarget.PROPERTY)
annotation class MappedCollection(val componentType: KClass<out Any>)
