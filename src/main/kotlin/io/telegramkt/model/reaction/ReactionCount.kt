package io.telegramkt.model.reaction

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReactionCount(
    @SerialName("type") val type: ReactionType,
    @SerialName("total_count") val totalCount: Int,
)
