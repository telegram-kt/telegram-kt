package io.telegramkt.model.reaction

import io.telegramkt.model.chat.Chat
import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageReactionUpdated(
    @SerialName("chat") val chat: Chat,
    @SerialName("message_id") val messageId: Int,
    @SerialName("user") val user: User? = null,
    @SerialName("actor_chat") val actorChat: Chat? = null,
    @SerialName("date") val date: Int,
    @SerialName("old_reaction") val oldReaction: List<ReactionType>,
    @SerialName("new_reaction") val newReaction: List<ReactionType>,
)
