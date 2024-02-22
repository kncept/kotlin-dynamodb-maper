package com.kncept.mapper

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import com.kncept.mapper.annotation.MappedCollection
import java.math.BigDecimal
import java.math.BigInteger
import java.security.SecureRandom
import java.time.*
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.function.Consumer
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DynamoDbObjectMapperTest {

  @Test
  fun canRegisterTypes() {
    val objectMapper = DynamoDbObjectMapper()
    val intTypeMapper = objectMapper.typeMapper(Int::class)
    assertNotNull(intTypeMapper)
    val thrown: Throwable = assertThrows { objectMapper.typeMapper(EmptyTypes::class) }
    assertNotNull(thrown)
    objectMapper.register(FakeEmptyTypesMapper())
    val emptyTypeMapper = objectMapper.typeMapper(EmptyTypes::class)
    assertNotNull(emptyTypeMapper)
  }

  //    @Test
  fun typeMapperForAllTypes() {
    val objectMapper = DynamoDbObjectMapper()
    PrimitiveTypes::class.declaredMemberProperties.forEach {
      val typeMapper = objectMapper.typeMapper(it.returnType as KClass<Any>)
      assertNotNull(typeMapper)
    }
  }

  @Test
  fun canReconstituteObjects() {
    val objectMapper = DynamoDbObjectMapper()

    val reconstitutableAsserter: Consumer<Any> = Consumer { original ->
      val mapped = objectMapper.toAttributes(original)
      val reconstituted = objectMapper.toItem(original::class, mapped)
      assertEquals(original, reconstituted)
    }

    reconstitutableAsserter.accept(PrimitiveTypes())
    reconstitutableAsserter.accept(EmptyTypes())
    reconstitutableAsserter.accept(JavaMathTypes())
    reconstitutableAsserter.accept(JavaUtilTypes())
    reconstitutableAsserter.accept(JavaTimeTypes())

    reconstitutableAsserter.accept(SetCollectionTypes())
    reconstitutableAsserter.accept(ListCollectionTypes())
  }

  class FakeEmptyTypesMapper : TypeMapper<EmptyTypes> {
    override fun type(): KClass<EmptyTypes> {
      return EmptyTypes::class
    }

    override fun attributeType(): KClass<out AttributeValue> {
      TODO("Not yet implemented")
    }

    override fun toType(attribute: AttributeValue, mapper: ObjectMapper): EmptyTypes {
      TODO("Not yet implemented")
    }

    override fun toAttribute(item: EmptyTypes, mapper: ObjectMapper): AttributeValue {
      TODO("Not yet implemented")
    }
  }

  data class EmptyTypes(
      val emptyString: String = "",
      val nullString: String? = null,
      val nullBool: Boolean? = null,
  )

  data class PrimitiveTypes(
      val string: String = UUID.randomUUID().toString(),
      val bool: Boolean = Math.random() < 0.5,
      val byte: Byte = (Math.random() * Byte.MAX_VALUE).toInt().toByte(),
      val short: Short = (Math.random() * Short.MAX_VALUE).toInt().toShort(),
      val int: Int = (Math.random() * Int.MAX_VALUE).toInt(),
      val long: Long = (Math.random() * Long.MAX_VALUE).toLong(),
      val byteArray: ByteArray =
          byteArrayOf(
              (Math.random() * Byte.MAX_VALUE).toInt().toByte(),
              (Math.random() * Byte.MAX_VALUE).toInt().toByte(),
              (Math.random() * Byte.MAX_VALUE).toInt().toByte(),
          )
  )

  data class JavaMathTypes(
      val bigInteger: BigInteger =
          BigInteger(
              80,
              0,
              SecureRandom()), // 'small' prime of at least 80 bits (bigger than Longs can be)
      val bigDecimal: BigDecimal =
          bigInteger
              .toBigDecimal()
              .add(BigDecimal("0.${(Math.random() * Long.MAX_VALUE).toLong()}")),
  )

  data class JavaUtilTypes(
      val uuid: UUID = UUID.randomUUID(),
      val ccy: Currency = Currency.getAvailableCurrencies().random(),
      val legacyDate: Date = Date(),
      val locale: Locale = Locale.getDefault(),
      val enLocale: Locale = Locale.forLanguageTag("en"),
      val enGbLocale: Locale = Locale.forLanguageTag("en-GB"),
      val enUsLocale: Locale = Locale.forLanguageTag("en-US"),
  )

  data class JavaTimeTypes(
      val unrestricted: LocalDateTime = LocalDateTime.now(Clock.systemUTC()),
      val toEpochSecond: LocalDateTime =
          LocalDateTime.now(Clock.systemUTC()).truncatedTo(ChronoUnit.SECONDS),
      val date: LocalDate = LocalDate.now().minusDays((Math.random() * Short.MAX_VALUE).toLong()),
      val epochSecondInstant: Instant = Instant.now().truncatedTo(ChronoUnit.SECONDS),
      val zonedDateTime: ZonedDateTime = ZonedDateTime.now(ZoneId.systemDefault()),
      val utcZoned: ZonedDateTime = ZonedDateTime.now(Clock.systemUTC()),
      val currentTime: LocalTime = LocalTime.now(),
      val currentTimeUtc: LocalTime = LocalTime.now(Clock.systemUTC()),
      val duration: Duration = Duration.ofSeconds((Math.random() * Short.MAX_VALUE).toLong()),
      val offsetDateTime: OffsetDateTime = OffsetDateTime.now(),
      val offsetTime: OffsetTime = OffsetTime.now(),
      val zone: ZoneId = ZoneId.systemDefault(),
      val zoneOffset: ZoneOffset =
          ZoneOffset.ofHoursMinutes((Math.random() * 12).toInt(), (Math.random() * 60).toInt())
  )

  data class SetCollectionTypes(
      @MappedCollection(String::class) val nullStrings: Set<String>? = null,
      @MappedCollection(String::class) val emptyNullableStrings: Set<String>? = setOf(),
      @MappedCollection(String::class) val emptyStrings: Set<String> = setOf(),
      @MappedCollection(String::class) val stringWithEmpty: Set<String> = setOf(""),
      @MappedCollection(String::class) val strings: Set<String> = setOf("1", "2"),
      @MappedCollection(String::class)
      val mutableStrings: MutableSet<String> = mutableSetOf("3", "4")
  )

  data class ListCollectionTypes(
      @MappedCollection(String::class) val nullStrings: List<String>? = null,
      @MappedCollection(String::class) val emptyNullableStrings: List<String>? = listOf(),
      @MappedCollection(String::class) val emptyStrings: List<String> = listOf(),
      @MappedCollection(String::class) val stringWithEmpty: List<String> = listOf(""),
      @MappedCollection(String::class) val strings: List<String> = listOf("1", "2"),
      @MappedCollection(String::class)
      val mutableStrings: MutableList<String> = mutableListOf("3", "4")
  )
}
