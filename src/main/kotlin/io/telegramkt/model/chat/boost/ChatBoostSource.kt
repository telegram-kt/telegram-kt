package io.telegramkt.model.chat.boost

import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@JsonClassDiscriminator("source")
sealed class ChatBoostSource {
    abstract val user: User

    @Serializable
    @SerialName("premium")
    data class Premium(
        override val user: User,
    ): ChatBoostSource()

    @Serializable
    @SerialName("gift_code")
    data class GiftCode(
        override val user: User,
    ): ChatBoostSource()

    @Serializable
    @SerialName("giveaway")
    data class Giveaway(
        @SerialName("giveaway_message_id") val giveawayMessageId: Int,
        override val user: User,
        @SerialName("prize_star_count") val prizeStarCount: Int? = null,
        @SerialName("is_unclaimed") val isUnclaimed: Boolean? = null
    ): ChatBoostSource()
}