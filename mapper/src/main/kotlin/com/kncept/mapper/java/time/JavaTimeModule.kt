package com.kncept.mapper.java.time

import com.kncept.mapper.TypeMapper
import com.kncept.mapper.TypeMapperModule

class JavaTimeModule : TypeMapperModule {

  var truncateTypesToEpochSecond = true

  override fun mappers(): List<TypeMapper<*>> {
    return listOf(
        LocalDateTimeMapper(),
        LocalDateMapper(),
        LocalTimeMapper(),
        InstantMapper(truncateTypesToEpochSecond),
        ZonedDateTimeMapper(),
        DurationMapper(),
        OffsetDateTimeMapper(),
        OffsetTimeMapper(),
        ZoneIdMapper(),
        ZoneOffsetMapper(),
    )
  }
}
