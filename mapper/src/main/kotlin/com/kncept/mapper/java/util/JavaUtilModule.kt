package com.kncept.mapper.java.util

import com.kncept.mapper.TypeMapper
import com.kncept.mapper.TypeMapperModule

class JavaUtilModule : TypeMapperModule {

  override fun mappers(): List<TypeMapper<*>> {
    return listOf(
        UUIDMapper(),
        CurrencyMapper(),
        DateMapper(),
        LocaleMapper(),
    )
  }
}
