package io.telegramkt.model.chat.business

import io.telegramkt.model.sticker.Sticker
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BusinessIntro(
    @SerialName("title") val title: String? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("sticker") val sticker: Sticker? = null,
)
