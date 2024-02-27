package com.kncept.mapper.primitives

import com.kncept.mapper.TypeMapper
import com.kncept.mapper.TypeMapperModule

class PrimitivesModule : TypeMapperModule {

  override fun mappers(): List<TypeMapper<*>> {
    return listOf(
        // simple primitives
        BooleanMapper(),
        ByteMapper(),
        ShortMapper(),
        IntMapper(),
        LongMapper(),
        FloatMapper(),
        DoubleMapper(),
        CharMapper(),

        // strings (!!)
        StringMapper(),

        // array types (N.B. NOT the kotlin.Array class)
        BooleanArrayMapper(),
        ByteArrayMapper(),
        CharArrayMapper(),
        DoubleArrayMapper(),
        FloatArrayMapper(),
        IntArrayMapper(),
        LongArrayMapper(),
        ShortArrayMapper(),
    )
  }
}
