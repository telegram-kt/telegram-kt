package io.telegramkt.model.keyboard.button

import io.telegramkt.model.web.WebAppInfo
import kotlinx.serialization.json.JsonClassDiscriminator

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@JsonClassDiscriminator("---")
sealed class KeyboardButton {
    abstract val text: String
    @SerialName("icon_custom_emoji_id") abstract val iconCustomEmojiId: String?

    @Serializable
    data class Simple(
        override val text: String,
        @SerialName("icon_custom_emoji_id") override val iconCustomEmojiId: String? = null
    ) : KeyboardButton()

    @Serializable
    data class RequestUsers(
        override val text: String,
        @SerialName("request_users") val requestUsers: KeyboardButtonRequestUsers,
        @SerialName("icon_custom_emoji_id") override val iconCustomEmojiId: String? = null
    ) : KeyboardButton()

    @Serializable
    data class RequestChat(
        override val text: String,
        @SerialName("request_chat") val requestChat: KeyboardButtonRequestChat,
        @SerialName("icon_custom_emoji_id") override val iconCustomEmojiId: String? = null
    ) : KeyboardButton()

    @Serializable
    data class RequestContact(
        override val text: String,
        @SerialName("request_contact") val requestContact: Boolean = true,
        @SerialName("icon_custom_emoji_id") override val iconCustomEmojiId: String? = null
    ) : KeyboardButton()

    @Serializable
    data class RequestLocation(
        override val text: String,
        @SerialName("request_location") val requestLocation: Boolean = true,
        @SerialName("icon_custom_emoji_id") override val iconCustomEmojiId: String? = null
    ) : KeyboardButton()

    @Serializable
    data class RequestPoll(
        override val text: String,
        @SerialName("request_poll") val requestPoll: KeyboardButtonPollType,
        @SerialName("icon_custom_emoji_id") override val iconCustomEmojiId: String? = null
    ) : KeyboardButton()

    @Serializable
    data class WebApp(
        override val text: String,
        @SerialName("web_app") val webApp: WebAppInfo,
        @SerialName("icon_custom_emoji_id") override val iconCustomEmojiId: String? = null
    ) : KeyboardButton()
}
