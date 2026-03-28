package io.telegramkt.model.user.shared

import io.telegramkt.model.photo.PhotoSize
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SharedUser(
    @SerialName("user_id") val userId: Int,
    @SerialName("first_name") val firstName: String? = null,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("photo") val photo: List<PhotoSize>? = null,
)
