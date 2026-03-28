package io.telegramkt.model.giveaway

import io.telegramkt.model.message.Message
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GiveawayCompleted(
    @SerialName("winner_count") val winnerCount: Int,
    @SerialName("unclaimed_prize_count") val unclaimedPrizeCount: Int? = null,
    @SerialName("giveaway_message") val giveawayMessage: Message? = null,
    @SerialName("is_star_giveaway") val isStarGiveaway: Boolean? = null,
)
