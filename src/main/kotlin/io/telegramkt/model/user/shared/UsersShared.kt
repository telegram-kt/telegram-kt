package io.telegramkt.model.user.shared

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsersShared(
    @SerialName("request_id") val requestId: Int,
    @SerialName("users") val users: List<SharedUser>
)
