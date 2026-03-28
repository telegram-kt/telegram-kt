package io.telegramkt.model.suggested

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuggestedPostParameters(
    @SerialName("price") val price: SuggestedPostPrice? = null,
    @SerialName("send_date") val sendDate: Int? = null,
)
