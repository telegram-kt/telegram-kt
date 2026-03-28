package io.telegramkt.model.chat.boost

import io.telegramkt.model.chat.Chat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatBoostRemoved(
    @SerialName("chat") val chat: Chat,
    @SerialName("boost_id") val boostId: String,
    @SerialName("remove_date") val removeDate: Int,
    @SerialName("source") val source: ChatBoostSource,
)
