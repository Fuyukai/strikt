package strikt

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.api.expectThrows
import strikt.assertions.isA

@DisplayName("throws assertion")
internal class Throws {
  @Test
  fun `throws passes if the action throws the expected exception`() {
    expectThrows<IllegalStateException> { error("o noes") }
  }

  @Test
  fun `throws passes if the action throws a sub-class of the expected exception`() {
    expectThrows<RuntimeException> { error("o noes") }
  }

  @Test
  fun `throws fails if the action does not throw any exception`() {
    assertThrows<AssertionError> {
      expectThrows<IllegalStateException> { }
    }.let { e ->
      val expected =
        """▼ Expect that Success(kotlin.Unit):
          |  ✗ failed with an exception"""
          .trimMargin()
      assertEquals(expected, e.message)
    }
  }

  @Test
  fun `throws fails if the action throws the wrong type of exception`() {
    assertThrows<AssertionError> {
      expectThrows<NullPointerException> { error("o noes") }
    }.let { e ->
      val expected =
        """▼ Expect that Failure(java.lang.IllegalStateException: o noes):
          |  ✓ failed with an exception
          |  ▼ caught exception:
          |    ✗ is an instance of java.lang.NullPointerException : found java.lang.IllegalStateException"""
          .trimMargin()
      assertEquals(expected, e.message)
    }
  }

  @Test
  fun `throws returns an assertion whose subject is the exception that was caught`() {
    expectThrows<IllegalStateException> { error("o noes") }
      .isA<IllegalStateException>()
  }

  @Test
  fun `expectThrows accepts a suspending lambda`() {
    expectThrows<IllegalStateException> { delayedException(IllegalStateException()) }
      .isA<IllegalStateException>()
  }
}

private suspend fun <T : Throwable> delayedException(input: T): T? =
  withContext(Dispatchers.Default) {
    throw input
  }
