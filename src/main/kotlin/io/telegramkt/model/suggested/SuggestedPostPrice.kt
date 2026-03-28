package io.telegramkt.model.suggested

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuggestedPostPrice(
    @SerialName("currency") val currency: String,
    @SerialName("amount") val amount: Int,
)