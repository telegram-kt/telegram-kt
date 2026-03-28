package io.telegramkt.model.reaction

import io.telegramkt.model.chat.Chat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageReactionCountUpdated(
    @SerialName("chat") val chat: Chat,
    @SerialName("message_id") val messageId: Int,
    @SerialName("reactions") val reactions: List<ReactionCount>
)
