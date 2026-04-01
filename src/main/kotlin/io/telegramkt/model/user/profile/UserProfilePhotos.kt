package io.telegramkt.model.user.profile

import io.telegramkt.model.photo.PhotoSize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfilePhotos(
    @SerialName("total_count") val totalCount: Int,
    @SerialName("photos") val photos: List<PhotoSize>,
)
