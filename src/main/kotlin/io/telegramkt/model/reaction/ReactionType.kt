package io.telegramkt.model.reaction

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@JsonClassDiscriminator("type")
sealed class ReactionType {

    @Serializable
    @SerialName("emoji")
    data class Emoji(
        val emoji: String
    ) : ReactionType()

    @Serializable
    @SerialName("custom_emoji")
    data class CustomEmoji(
        @SerialName("custom_emoji_id") val customEmojiId: String
    ) : ReactionType()

    @Serializable
    @SerialName("paid")
    object Paid : ReactionType()
}