package io.telegramkt.model.message

import io.telegramkt.model.chat.Chat
import io.telegramkt.model.chat.ChatType
import io.telegramkt.model.message.entity.MessageEntity
import io.telegramkt.model.user.User
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for the Message entity.
 */
class MessageTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun createBaseChat(): Chat = Chat(
        id = -1001234567890L,
        type = ChatType.SUPERGROUP,
        title = "Test Group"
    )

    private fun createBaseUser(): User = User(
        id = 123456789,
        isBot = false,
        firstName = "Test",
        username = "testuser"
    )

    @Test
    fun `message with text hasText returns true`() {
        val message = Message(
            id = 1,
            date = 1234567890,
            chat = createBaseChat(),
            text = "Hello, world!"
        )

        assertTrue(message.hasText)
    }

    @Test
    fun `message with empty text hasText returns false`() {
        val message = Message(
            id = 1,
            date = 1234567890,
            chat = createBaseChat(),
            text = ""
        )

        assertFalse(message.hasText)
    }

    @Test
    fun `message with null text hasText returns false`() {
        val message = Message(
            id = 1,
            date = 1234567890,
            chat = createBaseChat()
        )

        assertFalse(message.hasText)
    }

    @Test
    fun `message isEdited returns true when editDate is present`() {
        val message = Message(
            id = 1,
            date = 1234567890,
            chat = createBaseChat(),
            text = "Original",
            editDate = 1234567900
        )

        assertTrue(message.isEdited)
    }

    @Test
    fun `message isEdited returns false when editDate is null`() {
        val message = Message(
            id = 1,
            date = 1234567890,
            chat = createBaseChat(),
            text = "Not edited"
        )

        assertFalse(message.isEdited)
    }

    @Test
    fun `message isCommand returns true for bot command`() {
        val message = Message(
            id = 1,
            date = 1234567890,
            chat = createBaseChat(),
            text = "/start"
        )

        assertTrue(message.isCommand)
    }

    @Test
    fun `message isCommand returns false for regular text`() {
        val message = Message(
            id = 1,
            date = 1234567890,
            chat = createBaseChat(),
            text = "Hello"
        )

        assertFalse(message.isCommand)
    }

    @Test
    fun `message with photo hasMedia returns true`() {
        val message = Message(
            id = 1,
            date = 1234567890,
            chat = createBaseChat(),
            photo = listOf()
        )

        assertTrue(message.hasMedia)
    }

    @Test
    fun `message with text only hasMedia returns false`() {
        val message = Message(
            id = 1,
            date = 1234567890,
            chat = createBaseChat(),
            text = "Just text"
        )

        assertFalse(message.hasMedia)
    }

    @Test
    fun `message deserialization from JSON works`() {
        val jsonStr = """
            {
                "message_id": 1,
                "date": 1234567890,
                "chat": {
                    "id": -1001234567890,
                    "type": "supergroup",
                    "title": "Test"
                },
                "text": "Hello",
                "from": {
                    "id": 123456789,
                    "is_bot": false,
                    "first_name": "Test"
                }
            }
        """.trimIndent()

        val message = json.decodeFromString(Message.serializer(), jsonStr)

        assertEquals(1, message.id)
        assertEquals("Hello", message.text)
        assertEquals(-1001234567890L, message.chat.id)
        assertEquals(123456789, message.from?.id)
    }
}
