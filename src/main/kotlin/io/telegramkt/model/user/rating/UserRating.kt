package io.telegramkt.model.user.rating

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRating(
    @SerialName("level") val level: Int,
    @SerialName("rating") val rating: Int,
    @SerialName("current_level_rating") val currentLevelRating: Int,
    @SerialName("next_level_rating") val nextLevelRating: Int,
)
