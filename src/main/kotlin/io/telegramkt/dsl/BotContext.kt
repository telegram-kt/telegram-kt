package io.telegramkt.dsl

import io.telegramkt.client.TelegramBotClient
import io.telegramkt.model.ParseMode
import io.telegramkt.model.callback.CallbackQuery
import io.telegramkt.model.chat.Chat
import io.telegramkt.model.chat.ChatId
import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.inline.InlineQuery
import io.telegramkt.model.keyboard.reply.InlineKeyboardMarkup
import io.telegramkt.model.keyboard.reply.ReplyMarkup
import io.telegramkt.model.message.Message
import io.telegramkt.model.update.Update
import io.telegramkt.model.user.User
import kotlinx.coroutines.CoroutineScope
import kotlin.time.Duration

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

    suspend fun replyPhoto(
        photo: InputFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        replyMarkup: ReplyMarkup? = null,
        disableNotification: Boolean = false,
        protectContent: Boolean = false,
    ): Message = client.sendPhoto(
            chatId = chatId ?: throw IllegalStateException("No chat available"),
            photo = photo,
            caption = caption,
            parseMode = parseMode,
            replyMarkup = replyMarkup,
            disableNotification = disableNotification,
            protectContent = protectContent,
        )

    suspend fun replyVideo(
        video: InputFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        width: Int? = null,
        height: Int? = null,
        duration: Duration? = null,
        thumbnail: InputFile? = null,
        cover: InputFile? = null,
        startTimestamp: Duration? = null,
        replyMarkup: ReplyMarkup? = null,
        disableNotification: Boolean = false,
        protectContent: Boolean = false,
    ): Message = client.sendVideo(
        chatId = chatId ?: throw IllegalStateException("No chat available"),
        video = video,
        caption = caption,
        parseMode = parseMode,
        width = width,
        height = height,
        duration = duration,
        thumbnail = thumbnail,
        cover = cover,
        startTimestamp = startTimestamp,
        replyMarkup = replyMarkup,
        disableNotification = disableNotification,
        protectContent = protectContent,
    )

    suspend fun replyVideoNote(
        videoNote: InputFile,
        duration: Duration? = null,
        length: Int? = null,
        thumbnail: InputFile? = null,
        replyMarkup: ReplyMarkup? = null,
        disableNotification: Boolean = false,
        protectContent: Boolean = false,
    ): Message = client.sendVideoNote(
        chatId = chatId ?: throw IllegalStateException("No chat available"),
        videoNote = videoNote,
        duration = duration,
        length = length,
        thumbnail = thumbnail,
        disableNotification = disableNotification,
        protectContent = protectContent,
        replyMarkup = replyMarkup,
    )

    suspend fun replyAnimation(
        animation: InputFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        width: Int? = null,
        height: Int? = null,
        duration: Int? = null,
        thumbnail: InputFile? = null,
        replyMarkup: ReplyMarkup? = null,
        disableNotification: Boolean = false,
        protectContent: Boolean = false,
    ): Message = client.sendAnimation(
        chatId = chatId ?: throw IllegalStateException("No chat available"),
        animation = animation,
        caption = caption,
        parseMode = parseMode,
        width = width,
        height = height,
        duration = duration,
        thumbnail = thumbnail,
        replyMarkup = replyMarkup,
        disableNotification = disableNotification,
        protectContent = protectContent,
    )
    
    suspend fun replyAudio(
        audio: InputFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        duration: Duration? = null,
        title: String? = null,
        thumbnail: InputFile? = null,
        replyMarkup: ReplyMarkup? = null,
        disableNotification: Boolean = false,
        protectContent: Boolean = false,
    ): Message = client.sendAudio(
        chatId = chatId ?: throw IllegalStateException("No chat available"),
        audio = audio,
        caption = caption,
        parseMode = parseMode,
        duration = duration,
        title = title,
        thumbnail = thumbnail,
        replyMarkup = replyMarkup,
        disableNotification = disableNotification,
        protectContent = protectContent,
    )

    suspend fun replyVoice(
        voice: InputFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        duration: Duration? = null,
        replyMarkup: ReplyMarkup? = null,
        disableNotification: Boolean = false,
        protectContent: Boolean = false,
    ): Message = client.sendVoice(
        chatId = chatId ?: throw IllegalStateException("No chat available"),
        voice = voice,
        caption = caption,
        parseMode = parseMode,
        duration = duration,
        replyMarkup = replyMarkup,
        disableNotification = disableNotification,
        protectContent = protectContent,
    )

    suspend fun replyDocument(
        document: InputFile,
        caption: String? = null,
        parseMode: ParseMode? = null,
        thumbnail: InputFile? = null,
        replyMarkup: ReplyMarkup? = null,
        disableNotification: Boolean = false,
        protectContent: Boolean = false,
    ): Message = client.sendDocument(
        chatId = chatId ?: throw IllegalStateException("No chat available"),
        document = document,
        thumbnail = thumbnail,
        caption = caption,
        parseMode = parseMode,
        disableNotification = disableNotification,
        protectContent = protectContent,
        replyMarkup = replyMarkup,
    )

    suspend fun Message.editMessageText(
        text: String,
        parseMode: ParseMode? = null,
        replyMarkup: InlineKeyboardMarkup? = null,
    ): Message {
        return client.editMessageText(
            chatId = ChatId.ById(this.chat.id),
            messageId = this.id,
            text = text,
            parseMode = parseMode,
            replyMarkup = replyMarkup,
        )
    }

    suspend fun Message.deleteMessage() = client.deleteMessage(
        chatId = ChatId.ById(this.chat.id),
        this.id
    )

    suspend fun answerCallback(
        text: String? = null,
        showAlert: Boolean = false,
        url: String? = null,
        cacheTime: Duration? = null,
    ): Boolean {
        return callbackQuery?.let { cq ->
            client.answerCallbackQuery(
                callbackQueryId = cq.id,
                text = text,
                showAlert = showAlert,
                url = url,
                cacheTime = cacheTime,
            )
        } ?: false
    }

    fun isCommand(cmd: String): Boolean {
        return message?.text?.startsWith("/$cmd") == true ||
                message?.text?.startsWith("/$cmd@") == true
    }
}