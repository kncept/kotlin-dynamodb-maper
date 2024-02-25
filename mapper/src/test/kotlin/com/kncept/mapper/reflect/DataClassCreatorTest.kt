package com.kncept.mapper.reflect

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class DataClassCreatorTest {

  companion object {
    @JvmStatic
    fun implementations(): List<DataClassCreator<SampleType>> {
      return listOf(
          ReflectiveDataClassCreator(SampleType::class, true),
          ReflectiveDataClassCreator(SampleType::class, false),
      )
    }
  }

  data class SampleType(
      val first: String,
      val second: String?,
      val third: Boolean,
  )

  @ParameterizedTest
  @MethodSource("implementations")
  fun typeIsCorrect(creator: DataClassCreator<SampleType>) {
    assertEquals(SampleType::class, creator.type())
  }

  @ParameterizedTest
  @MethodSource("implementations")
  fun canListParamaters(creator: DataClassCreator<SampleType>) {
    val constructorParams = creator.types()
    assertTrue(constructorParams.containsKey("first"))
    assertTrue(constructorParams.containsKey("second"))
    assertTrue(constructorParams.containsKey("third"))
  }

  @ParameterizedTest
  @MethodSource("implementations")
  fun canReadValues(creator: DataClassCreator<SampleType>) {
    val sample = SampleType("firstString", null, false)
    val values = creator.values(sample)
    assertEquals(3, values.size)
    assertEquals("firstString", values["first"])
    assertTrue(values.containsKey("second"))
    assertNull(values["second"])
    assertFalse(values["third"] as Boolean)
  }

  @ParameterizedTest
  @MethodSource("implementations")
  fun canCreateWithAllParams(creator: DataClassCreator<SampleType>) {
    val params = mapOf("first" to "first arg", "second" to null, "third" to true)
    val created = creator.create(params)
    assertNotNull(created)
    assertEquals("first arg", created.first)
    assertNull(created.second)
    assertTrue(created.third)
  }

  @ParameterizedTest
  @MethodSource("implementations")
  fun canCreateWithOmittedNullParams(creator: DataClassCreator<SampleType>) {
    val params = mapOf("first" to "first arg", "third" to true)
    val created = creator.create(params)
    assertNotNull(created)
    assertEquals("first arg", created.first)
    assertNull(created.second)
    assertTrue(created.third)
  }
}
