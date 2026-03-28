package io.telegramkt.model.giveaway

import io.telegramkt.model.chat.Chat
import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GiveawayWinners(
    @SerialName("chat") val chat: Chat,
    @SerialName("giveaway_message_id") val giveawayMessageId: Int,
    @SerialName("winners_selection_date") val winnersSelectionDate: Int,
    @SerialName("winner_count") val winnerCount: Int,
    @SerialName("winners") val winners: List<User>,
    @SerialName("additional_chat_count") val additionalChatCount: Int? = null,
    @SerialName("prize_star_count") val prizeStarCount: Int? = null,
    @SerialName("premium_subscription_month_count") val premiumSubscriptionMonthCount: Int?  = null,
    @SerialName("unclaimed_prize_count") val unclaimedPrizeCount: Int? = null,
    @SerialName("only_new_members") val onlyNewMembers: Boolean? = null,
    @SerialName("was_refunded") val wasRefunded: Boolean? = null,
    @SerialName("prize_description") val prizeDescription: String? = null,
)
