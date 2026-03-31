package io.telegramkt.model.poll.input

import io.telegramkt.model.ParseMode
import io.telegramkt.model.message.entity.MessageEntity

class InputPollOptionBuilder(val questionText: String) {
    var textParseMode: ParseMode? = null
    var textEntities: List<MessageEntity>? = null

    fun build() = InputPollOption(questionText, textParseMode, textEntities)
}