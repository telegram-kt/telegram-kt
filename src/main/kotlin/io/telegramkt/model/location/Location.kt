package io.telegramkt.model.location

import io.telegramkt.serialization.duration.DurationSecondsSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration

@Serializable
data class Location(
    @SerialName("latitude") val latitude: Float,
    @SerialName("longitude") val longitude: Float,
    @SerialName("horizontal_accuracy") val horizontalAccuracy: Float? = null,
    @SerialName("live_period")
    @Serializable(with = DurationSecondsSerializer::class)
    val livePeriod: Duration? = null,
    @SerialName("heading") val heading: Int? = null,
    @SerialName("proximity_alert_radius") val proximityAlertRadius: Int? = null,
)
