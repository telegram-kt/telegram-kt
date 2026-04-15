package io.telegramkt.dsl

import io.telegramkt.model.chat.Chat
import io.telegramkt.model.chat.ChatType
import io.telegramkt.model.message.Message
import io.telegramkt.model.message.MessageOrigin
import io.telegramkt.model.user.User
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Unit tests for BotContext and message handling.
 */
class BotContextTest {

    @Test
    fun `message with command starts with slash`() {
        val chat = Chat(
            id = -1001234567890L,
            type = ChatType.PRIVATE,
            firstName = "Test"
        )
        val user = User(id = 12345, isBot = false, firstName = "Test")
        val message = Message(
            id = 1,
            date = 1234567890,
            chat = chat,
            from = user,
            text = "/start test"
        )

        assertTrue(message.text!!.startsWith("/"))
        assertEquals("/start test", message.text)
    }

    @Test
    fun `message forward origin is set correctly`() {
        val chat = Chat(
            id = -1001234567890L,
            type = ChatType.PRIVATE,
            firstName = "Test"
        )
        val user = User(id = 12345, isBot = false, firstName = "Test")
        val forwardOrigin = MessageOrigin(
            type = "user"
        )
        val message = Message(
            id = 1,
            date = 1234567890,
            chat = chat,
            from = user,
            text = "Forwarded",
            forwardOrigin = forwardOrigin
        )

        assertTrue(message.isForwarded)
        assertEquals(forwardOrigin, message.forwardOrigin)
    }

    @Test
    fun `message without forward origin isForwarded returns false`() {
        val chat = Chat(
            id = -1001234567890L,
            type = ChatType.PRIVATE,
            firstName = "Test"
        )
        val message = Message(
            id = 1,
            date = 1234567890,
            chat = chat,
            text = "Original"
        )

        assertFalse(message.isForwarded)
    }

    @Test
    fun `message with editDate isEdited returns true`() {
        val chat = Chat(
            id = -1001234567890L,
            type = ChatType.PRIVATE,
            firstName = "Test"
        )
        val message = Message(
            id = 1,
            date = 1234567890,
            chat = chat,
            text = "Edited message",
            editDate = 1234567900
        )

        assertTrue(message.isEdited)
    }
}
