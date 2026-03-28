package io.telegramkt.model.chat.background

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@JsonClassDiscriminator("type")
sealed class BackgroundFill {

    @Serializable
    @SerialName("solid")
    data class Solid(
        @SerialName("color") val color: Int
    ) : BackgroundFill()

    @Serializable
    @SerialName("gradient")
    data class Gradient(
        @SerialName("top_color") val topColor: Int,
        @SerialName("bottom_color") val bottomColor: Int,
        @SerialName("rotation_angle") val rotationAngle: Int,
    ) : BackgroundFill()

    @Serializable
    @SerialName("freeform_gradient")
    data class FreeformGradient(
        @SerialName("colors") val colors: List<Int>
    ) : BackgroundFill()
}