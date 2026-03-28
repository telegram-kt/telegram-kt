package io.telegramkt.model.chat.video

import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoChatParticipantsInvited(
    @SerialName("users") val users: List<User>
)
