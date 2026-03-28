package io.telegramkt.client

import io.telegramkt.api.TelegramResponse
import io.telegramkt.api.ResponseParameters
import io.telegramkt.exception.TelegramApiException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.*

/**
 * Unit-tests for TelegramParametersBuilder and TelegramResponse.
 */
class ParametersAndResponseTest {

    private val json = Json { ignoreUnknownKeys = true }

    /* ---------- TelegramParametersBuilder ---------- */

    @Test
    fun `null parameters are omitted from the map`() {
        val builder = TelegramParametersBuilder(json)
        builder.parameter("key", null as String?)
        assertFalse(builder.params.containsKey("key"))
    }

    @Test
    fun `non-null parameters are kept`() {
        val builder = TelegramParametersBuilder(json)
        builder.parameter("key", "value")
        assertEquals("value", builder.params["key"])
    }

    @Test
    fun `jsonParameter encodes objects correctly`() {
        @Serializable
        data class Foo(val bar: String)

        val builder = TelegramParametersBuilder(json)
        builder.jsonParameter("foo", Foo("baz"))

        val encoded = builder.params["foo"] as? String
        assertNotNull(encoded)
        assertTrue(encoded.contains("baz"))
    }

    /* ---------- TelegramResponse ---------- */

    @Test
    fun `requireSuccess does not throw when ok is true`() {
        val resp = TelegramResponse(ok = true, result = "data")
        assertEquals("data", resp.requireSuccess().requireResult())
    }

    @Test
    fun `requireSuccess throws TelegramApiException when ok is false`() {
        val resp = TelegramResponse<String>(
            ok = false,
            errorCode = 400,
            description = "Bad Request: message not found",
            parameters = ResponseParameters(retryAfter = 30)
        )
        val ex = assertFailsWith<TelegramApiException> { resp.requireSuccess() }
        assertEquals(400, ex.errorCode)
        assertEquals("Telegram API Error [400]: Bad Request: message not found", ex.message)
        assertEquals(30, ex.parameters?.retryAfter)
    }

    @Test
    fun `requireResult throws when result is null despite ok true`() {
        val resp = TelegramResponse<String>(ok = true, result = null)
        val ex = assertFailsWith<TelegramApiException> { resp.requireResult() }
        assertTrue(ex.message!!.contains("Result is null"))
    }
}