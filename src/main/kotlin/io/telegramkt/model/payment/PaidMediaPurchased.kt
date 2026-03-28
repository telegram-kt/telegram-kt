package io.telegramkt.model.payment

import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaidMediaPurchased(
    @SerialName("from") val from: User,
    @SerialName("paid_media_payload") val paidMediaPayload: String,
)
