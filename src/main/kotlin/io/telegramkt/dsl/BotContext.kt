package io.telegramkt.dsl

import io.telegramkt.client.TelegramBotClient
import io.telegramkt.model.ParseMode
import io.telegramkt.model.callback.CallbackQuery
import io.telegramkt.model.chat.Chat
import io.telegramkt.model.chat.ChatId
import io.telegramkt.model.inline.InlineQuery
import io.telegramkt.model.keyboard.reply.InlineKeyboardMarkup
import io.telegramkt.model.keyboard.reply.ReplyMarkup
import io.telegramkt.model.message.Message
import io.telegramkt.model.update.Update
import io.telegramkt.model.user.User
import kotlinx.coroutines.CoroutineScope

class BotContext(
    val client: TelegramBotClient,
    val update: Update,
    val scope: CoroutineScope,
) {
    val message: Message? get() = update.message
    val callbackQuery: CallbackQuery? get() = update.callbackQuery
    val inlineQuery: InlineQuery? get() = update.inlineQuery

    val from: User? get() = message?.from ?: callbackQuery?.from
    val chat: Chat? get() = message?.chat
    val chatId: ChatId? get() = chat?.let { ChatId.ById(it.id) }

    val text: String? get() = message?.text ?: callbackQuery?.data
    val args: List<String> get() = text?.split(" ").orEmpty().drop(1)

    suspend fun reply(
        text: String,
        parseMode: ParseMode? = null,
        replyMarkup: ReplyMarkup? = null,
        disableNotification: Boolean = false,
    ): Message {
        return client.sendMessage(
            chatId = chatId ?: throw IllegalStateException("No chat available"),
            text = text,
            parseMode = parseMode,
            replyMarkup = replyMarkup,
            disableNotification = disableNotification,
        )
    }

    suspend fun replyWithKeyboard(
        text: String,
        keyboard: InlineKeyboardMarkup,
        parseMode: ParseMode? = null,
    ): Message {
        return reply(text, parseMode, keyboard)
    }

    suspend fun editMessage(
        text: String,
        parseMode: ParseMode? = null,
        replyMarkup: InlineKeyboardMarkup? = null,
    ) {
        message?.let { msg ->
            client.editMessageText(
                chatId = chatId,
                messageId = msg.id,
                text = text,
                parseMode = parseMode,
                replyMarkup = replyMarkup,
            )
        }
    }

    suspend fun answerCallback(
        text: String? = null,
        showAlert: Boolean = false,
        url: String? = null,
        cacheTime: Int? = null,
    ) {
        callbackQuery?.let { cq ->
            client.answerCallbackQuery(
                callbackQueryId = cq.id,
                text = text,
                showAlert = showAlert,
                url = url,
                cacheTime = cacheTime,
            )
        }
    }

    fun isCommand(cmd: String): Boolean {
        return message?.text?.startsWith("/$cmd") == true ||
                message?.text?.startsWith("/$cmd@") == true
    }
}