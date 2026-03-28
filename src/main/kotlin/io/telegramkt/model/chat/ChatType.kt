package io.telegramkt.model.chat

import kotlinx.serialization.SerialName

enum class ChatType {
    @SerialName("private") PRIVATE,
    @SerialName("group") GROUP,
    @SerialName("supergroup") SUPERGROUP,
    @SerialName("channel") CHANNEL,
}