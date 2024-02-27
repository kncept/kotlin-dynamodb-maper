package com.kncept.mapper

/**
 * A set of type mappers<br> Note that individual modules may have their own configuration
 * options<br>
 */
interface TypeMapperModule {
  fun mappers(): List<TypeMapper<*>>
}
