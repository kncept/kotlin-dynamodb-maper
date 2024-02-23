package com.kncept.mapper

/**
 * A set of type mappers<br> Note that individual modules may have their own configuration options
 */
interface TypeMapperModule {
  fun mappers(): List<TypeMapper<*>>
}
