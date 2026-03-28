package io.telegramkt.model.inline

import io.telegramkt.model.location.Location
import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InlineQuery(
    @SerialName("id") val id: Int,
    @SerialName("from") val from: User,
    @SerialName("query") val query: String,
    @SerialName("offset") val offset: String,
    @SerialName("chat_type") val chatType: String? = null,
    @SerialName("location") val location: Location? = null,
)
