package io.telegramkt.model.message.input

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InputLocationMessageContent(
    @SerialName("latitude") val latitude: Float,
    @SerialName("longitude") val longitude: Float,
    @SerialName("horizontal_accuracy") val horizontalAccuracy: Float? = null,
    @SerialName("live_period") val livePeriod: Int? = null,
    @SerialName("heading") val heading: Int? = null,
    @SerialName("proximity_alert_radius") val proximityAlertRadius: Int? = null,
) : InputMessageContent