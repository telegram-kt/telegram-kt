package io.telegramkt.model.keyboard.inline

import io.telegramkt.model.game.CallbackGameInfo
import io.telegramkt.model.keyboard.button.CopyTextButton
import io.telegramkt.model.web.LoginUrlInfo
import io.telegramkt.model.web.WebAppInfo
import io.telegramkt.serialization.InlineKeyboardButtonSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = InlineKeyboardButtonSerializer::class)
sealed class InlineKeyboardButton {
    abstract val text: String
    @SerialName("icon_custom_emoji_id") abstract val iconCustomEmojiId: String?
    abstract val style: String?

    @Serializable
    data class Url(
        override val text: String,
        val url: String,
        override val style: String? = null,
        @SerialName("icon_custom_emoji_id") override val iconCustomEmojiId: String? = null,
    ) : InlineKeyboardButton()

    @Serializable
    data class Callback(
        override val text: String,
        @SerialName("callback_data") val callbackData: String,
        override val style: String? = null,
        @SerialName("icon_custom_emoji_id") override val iconCustomEmojiId: String? = null,
    ) : InlineKeyboardButton()

    @Serializable
    data class WebApp(
        override val text: String,
        @SerialName("web_app") val webApp: WebAppInfo,
        override val style: String? = null,
        @SerialName("icon_custom_emoji_id") override val iconCustomEmojiId: String? = null,
    ) : InlineKeyboardButton()

    @Serializable
    data class LoginUrl(
        override val text: String,
        @SerialName("login_url") val loginUrl: LoginUrlInfo,
        override val style: String? = null,
        @SerialName("icon_custom_emoji_id") override val iconCustomEmojiId: String? = null,
    ) : InlineKeyboardButton()

    @Serializable
    data class SwitchInlineQuery(
        override val text: String,
        @SerialName("switch_inline_query") val switchInlineQuery: String,
        override val style: String? = null,
        @SerialName("icon_custom_emoji_id") override val iconCustomEmojiId: String? = null,
    ) : InlineKeyboardButton()

    @Serializable
    data class SwitchInlineQueryCurrentChat(
        override val text: String,
        @SerialName("switch_inline_query_current_chat") val switchInlineQueryCurrentChat: String,
        override val style: String? = null,
        @SerialName("icon_custom_emoji_id") override val iconCustomEmojiId: String? = null,
    ) : InlineKeyboardButton()

    @Serializable
    data class SwitchInlineQueryChosenChat(
        override val text: String,
        @SerialName("switch_inline_query_chosen_chat") val switchInlineQueryChosenChat: SwitchInlineQueryChosenChatInfo,
        override val style: String? = null,
        @SerialName("icon_custom_emoji_id") override val iconCustomEmojiId: String? = null,
    ) : InlineKeyboardButton()

    @Serializable
    data class CopyText(
        override val text: String,
        @SerialName("copy_text") val copyText: CopyTextButton,
        override val style: String? = null,
        @SerialName("icon_custom_emoji_id") override val iconCustomEmojiId: String? = null,
    ) : InlineKeyboardButton()

    @Serializable
    data class CallbackGame(
        override val text: String,
        @SerialName("callback_game") val callbackGame: CallbackGameInfo,
        override val style: String? = null,
        @SerialName("icon_custom_emoji_id") override val iconCustomEmojiId: String? = null,
    ) : InlineKeyboardButton()

    @Serializable
    data class Pay(
        override val text: String,
        val pay: Boolean = true,
        override val style: String? = null,
        @SerialName("icon_custom_emoji_id") override val iconCustomEmojiId: String? = null,
    ) : InlineKeyboardButton()
}
