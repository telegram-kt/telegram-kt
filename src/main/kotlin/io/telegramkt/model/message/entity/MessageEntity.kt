package io.telegramkt.model.message.entity

import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageEntity(
    @SerialName("type") val type: String,
    @SerialName("offset") val offset: Int,
    @SerialName("length") val length: Int,
    @SerialName("url") val url: String? = null,
    @SerialName("user") val user: User? = null,
    @SerialName("language") val language: String? = null,
    @SerialName("custom_emoji_id") val customEmojiId: String? = null,
    @SerialName("unix_time") val unixTime: Int? = null,
    @SerialName("date_time_format") val dateTimeFormat: String? = null
)