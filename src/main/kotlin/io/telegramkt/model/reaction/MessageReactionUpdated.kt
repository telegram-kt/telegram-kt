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
) {
    val currentReactionsCount: Int get() = newReaction.size
    val previousReactionsCount: Int get() = oldReaction.size

    fun addedReactions(): List<ReactionType> = newReaction - oldReaction.toSet()
    fun deletedReactions(): List<ReactionType> = oldReaction - newReaction.toSet()

    fun addedReaction(): ReactionType? = (newReaction - oldReaction.toSet()).firstOrNull()
    fun deletedReaction(): ReactionType? = (oldReaction - newReaction.toSet()).firstOrNull()

    fun wasAdded(reaction: ReactionType): Boolean = reaction in addedReactions()
    fun wasDeleted(reaction: ReactionType): Boolean = reaction in deletedReactions()
}