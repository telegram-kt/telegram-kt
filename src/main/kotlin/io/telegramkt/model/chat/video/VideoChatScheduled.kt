package io.telegramkt.model.chat.video

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoChatScheduled(
    @SerialName("start_date") val startDate: Int
)
