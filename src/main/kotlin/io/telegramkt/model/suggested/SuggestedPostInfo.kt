package io.telegramkt.model.suggested

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuggestedPostInfo(
    @SerialName("state") val state: String,
    @SerialName("price") val price: SuggestedPostPrice? = null,
    @SerialName("send_date") val sendDate: Int? = null,
)
