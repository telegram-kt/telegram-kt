package io.telegramkt.model.web

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebAppInfo(
    @SerialName("url") val url: String
)
