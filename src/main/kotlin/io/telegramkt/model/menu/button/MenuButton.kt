package io.telegramkt.model.menu.button

import io.telegramkt.model.web.WebAppInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class MenuButton {

    @Serializable
    @SerialName("default")
    data object  Default : MenuButton()

    @Serializable
    @SerialName("commands")
    data object Commands : MenuButton()

    @Serializable
    @SerialName("web_app")
    data class WebApp(
        @SerialName("text") val text: String,
        @SerialName("web_app") val webApp: WebAppInfo,
    ): MenuButton()
}