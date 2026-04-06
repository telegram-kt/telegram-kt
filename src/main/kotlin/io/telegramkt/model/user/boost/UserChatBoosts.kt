package io.telegramkt.model.user.boost

import io.telegramkt.model.chat.boost.ChatBoost
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserChatBoosts(
    @SerialName("boosts") val boosts: List<ChatBoost>
)
