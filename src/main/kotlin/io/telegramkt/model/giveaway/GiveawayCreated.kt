package io.telegramkt.model.giveaway

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GiveawayCreated(
    @SerialName("prize_star_count") val prizeStarCount: Int? = null,
)
