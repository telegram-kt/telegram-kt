package io.telegramkt.model.giveaway

import io.telegramkt.model.chat.Chat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Giveaway(
    @SerialName("chats") val chats: List<Chat>,
    @SerialName("winners_selection_date") val winnersSelectionDate: Int,
    @SerialName("winner_count") val winnerCount: Int,
    @SerialName("only_new_members") val onlyNewMembers: Boolean? = null,
    @SerialName("has_public_winners") val hasPublicWinners: Boolean? = null,
    @SerialName("prize_description") val prizeDescription: String? = null,
    @SerialName("country_codes") val countryCodes: List<String>? = null,
    @SerialName("prize_star_count") val prizeStarCount: Int? = null,
    @SerialName("premium_subscription_month_count") val premiumSubscriptionMonthCount: Int? = null,
)
