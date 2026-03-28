package io.telegramkt.model.mask

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MaskPosition(
    @SerialName("point") val point: String,
    @SerialName("x_shift") val xShift: Float,
    @SerialName("y_shift") val yShift: Float,
    @SerialName("scale") val scale: Float
)
