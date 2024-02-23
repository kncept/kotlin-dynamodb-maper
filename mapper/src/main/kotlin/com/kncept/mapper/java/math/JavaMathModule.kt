package com.kncept.mapper.java.math

import com.kncept.mapper.TypeMapper
import com.kncept.mapper.TypeMapperModule

class JavaMathModule : TypeMapperModule {

  override fun mappers(): List<TypeMapper<*>> {
    return listOf(
        BigIntegerMapper(),
        BigDecimalMapper(),
    )
  }
}
