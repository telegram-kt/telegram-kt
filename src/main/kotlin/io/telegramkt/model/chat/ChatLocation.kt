package io.telegramkt.model.chat

import io.telegramkt.model.location.Location
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatLocation(
    @SerialName("location") val location: Location,
    @SerialName("address") val address: String,
)
