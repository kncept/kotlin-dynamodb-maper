package com.kncept.mapper.annotation

import com.kncept.mapper.TypeMapper
import kotlin.reflect.KClass

/**
 * Annotation that ties a specific mapper to a class or field<br> Useful for using the same kotlin
 * type for different dynamo db types<br>
 *
 * eg: Using a LocalDateTime as a readable string for 'created', but using it for epoch second in
 * 'ttl'.
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class MappedBy(val typeMapper: KClass<out TypeMapper<out Any>>)
