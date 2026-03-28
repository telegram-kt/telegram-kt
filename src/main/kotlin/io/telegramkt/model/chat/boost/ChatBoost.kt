package io.telegramkt.model.chat.boost

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatBoost(
    @SerialName("boost_id") val boostId: String,
    @SerialName("add_date") val addDate: Int,
    @SerialName("expiration_date") val expirationDate: Int,
    @SerialName("source") val source: ChatBoostSource
)
