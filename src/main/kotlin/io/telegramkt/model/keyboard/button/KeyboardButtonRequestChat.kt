package io.telegramkt.model.keyboard.button

import io.telegramkt.model.chat.administrator.ChatAdministratorRights
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KeyboardButtonRequestChat(
    @SerialName("request_id") val requestId: Int,
    @SerialName("chat_is_channel") val chatIsChannel: Boolean,
    @SerialName("chat_is_forum") val chatIsForum: Boolean? = null,
    @SerialName("chat_has_username") val chatHasUsername: Boolean? = null,
    @SerialName("chat_is_created") val chatIsCreated: Boolean? = null,
    @SerialName("user_administrator_rights") val userAdministratorRights: ChatAdministratorRights? = null,
    @SerialName("bot_administrator_rights") val botAdministratorRights: ChatAdministratorRights? = null,
    @SerialName("bot_is_member") val botIsMember: Boolean? = null,
    @SerialName("request_title") val requestTitle: Boolean? = null,
    @SerialName("request_username") val requestUsername: Boolean? = null,
    @SerialName("request_photo") val requestPhoto: Boolean? = null
)
