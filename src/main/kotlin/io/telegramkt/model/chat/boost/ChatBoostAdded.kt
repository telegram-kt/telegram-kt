package io.telegramkt.model.chat.boost

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatBoostAdded(
    @SerialName("boost_count") val boostCount: Int
)
