package com.kncept.mapper.primitives

import com.kncept.mapper.TypeMapper
import com.kncept.mapper.TypeMapperModule

class PrimitivesModule : TypeMapperModule {

  override fun mappers(): List<TypeMapper<*>> {
    return listOf(
        ByteArrayMapper(),
        BooleanMapper(),
        ByteMapper(),
        ShortMapper(),
        IntMapper(),
        LongMapper(),
        FloatMapper(),
        DoubleMapper(),
        CharMapper(),
        StringMapper(),
    )
  }
}
