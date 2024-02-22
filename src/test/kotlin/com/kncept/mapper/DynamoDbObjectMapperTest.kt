package com.kncept.mapper

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import java.math.BigDecimal
import java.math.BigInteger
import java.security.SecureRandom
import java.util.UUID
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
  }

  class FakeEmptyTypesMapper : TypeMapper<EmptyTypes> {
    override fun type(): KClass<EmptyTypes> {
      return EmptyTypes::class
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
}
