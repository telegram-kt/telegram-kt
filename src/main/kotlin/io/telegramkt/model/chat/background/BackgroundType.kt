package io.telegramkt.model.chat.background

import io.telegramkt.model.document.Document
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@JsonClassDiscriminator("type")
sealed class BackgroundType {

    @Serializable
    @SerialName("fill")
    data class Fill(
        @SerialName("fill") val fill: BackgroundFill,
        @SerialName("dark_theme_dimming") val darkThemeDimming: Int,
    ) : BackgroundType()

    @Serializable
    @SerialName("wallpaper")
    data class Wallpaper(
        @SerialName("document") val document: Document,
        @SerialName("dark_theme_dimming") val darkThemeDimming: Int,
        @SerialName("is_blurred") val isBlurred: Boolean? = null,
        @SerialName("is_moving") val isMoving: Boolean? = null,
    ) : BackgroundType()

    @Serializable
    @SerialName("pattern")
    data class Pattern(
        @SerialName("document") val document: Document,
        @SerialName("fill") val fill: BackgroundFill,
        @SerialName("intensity") val intensity: Int,
        @SerialName("is_inverted") val isInverted: Boolean? = null,
        @SerialName("is_moving") val isMoving: Boolean? = null,
    ) : BackgroundType()

    @Serializable
    @SerialName("chat_theme")
    data class ChatTheme(
        @SerialName("theme_name") val themeName: String
    ) : BackgroundType()
}
