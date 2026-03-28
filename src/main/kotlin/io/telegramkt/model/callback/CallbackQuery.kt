package io.telegramkt.model.callback

import io.telegramkt.model.message.MaybeInaccessibleMessage
import io.telegramkt.model.user.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CallbackQuery(
    @SerialName("id") val id: String,
    @SerialName("from") val from: User,
    @SerialName("message") val message: MaybeInaccessibleMessage? = null,
    @SerialName("inline_message_id") val inlineMessageId: String? = null,
    @SerialName("chat_instance") val chatInstance: String,
    @SerialName("data") val data: String? = null,
    @SerialName("game_short_name") val gameShortName: String? = null,
)
