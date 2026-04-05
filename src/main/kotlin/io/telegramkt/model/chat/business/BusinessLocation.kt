package io.telegramkt.model.chat.business

import io.telegramkt.model.location.Location
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BusinessLocation(
    @SerialName("address") val address: String,
    @SerialName("location") val location: Location? = null,
)
