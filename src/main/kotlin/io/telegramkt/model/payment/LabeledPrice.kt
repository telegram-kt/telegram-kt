package io.telegramkt.model.payment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LabeledPrice(
    @SerialName("label") val label: String,
    @SerialName("amount") val amount: Int,
)
