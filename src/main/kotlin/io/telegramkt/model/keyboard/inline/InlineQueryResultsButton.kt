package io.telegramkt.model.keyboard.inline

import io.telegramkt.model.web.WebAppInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InlineQueryResultsButton(
    @SerialName("text") val text: String,
    @SerialName("web_app") val webApp: WebAppInfo? = null,
    @SerialName("start_parameter") val startParameter: String? = null,
)