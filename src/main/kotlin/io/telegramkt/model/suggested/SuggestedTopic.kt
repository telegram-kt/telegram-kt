package io.telegramkt.model.suggested

import io.telegramkt.model.chat.Chat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Describes a suggested topic in a chat.
 *
 * See https://core.telegram.org/bots/api#suggestedtopic
 */
@Serializable
data class SuggestedTopic(
    @SerialName("chat") val chat: Chat,
    @SerialName("message_thread_id") val messageThreadId: Int? = null,
    @SerialName("post") val post: SuggestedPostInfo? = null,
)
