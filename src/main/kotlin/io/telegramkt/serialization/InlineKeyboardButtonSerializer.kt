package io.telegramkt.serialization

import io.telegramkt.model.keyboard.inline.InlineKeyboardButton
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

object InlineKeyboardButtonSerializer : JsonContentPolymorphicSerializer<InlineKeyboardButton>(InlineKeyboardButton::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<InlineKeyboardButton> {
        val jsonObject = element.jsonObject

        return when {
            "url" in jsonObject -> InlineKeyboardButton.Url.serializer()
            "callback_data" in jsonObject -> InlineKeyboardButton.Callback.serializer()
            "web_app" in jsonObject -> InlineKeyboardButton.WebApp.serializer()
            "login_url" in jsonObject -> InlineKeyboardButton.LoginUrl.serializer()
            "switch_inline_query" in jsonObject -> InlineKeyboardButton.SwitchInlineQuery.serializer()
            "switch_inline_query_current_chat" in jsonObject -> InlineKeyboardButton.SwitchInlineQueryCurrentChat.serializer()
            "copy_text" in jsonObject -> InlineKeyboardButton.CopyText.serializer()
            "callback_game" in jsonObject -> InlineKeyboardButton.CallbackGame.serializer()
            "pay" in jsonObject -> InlineKeyboardButton.Pay.serializer()
            else -> throw SerializationException("Unknown inline button type: $element")
        }
    }
}