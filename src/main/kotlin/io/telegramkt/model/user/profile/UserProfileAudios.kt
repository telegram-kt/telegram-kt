package io.telegramkt.model.user.profile

import io.telegramkt.model.audio.Audio
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileAudios(
    @SerialName("total_count") val totalCount: Int,
    @SerialName("audios") val audios: List<Audio>,
)
