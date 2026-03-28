package io.telegramkt.model.proximity

import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProximityAlertTriggered(
    @SerialName("traveler") val traveler: User,
    @SerialName("watcher") val watcher: User,
    @SerialName("distance") val distance: Int
)
