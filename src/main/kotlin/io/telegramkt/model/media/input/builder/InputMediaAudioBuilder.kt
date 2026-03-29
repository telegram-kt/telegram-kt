package io.telegramkt.model.media.input.builder

import io.telegramkt.model.ParseMode
import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.media.input.InputMediaAudio
import io.telegramkt.model.message.entity.MessageEntity

class InputMediaAudioBuilder(private val file: InputFile) {
    val thumbnail: InputFile? = null
    val caption: String? = null
    val parseMode: ParseMode? = null
    val captionEntities: List<MessageEntity>? = null
    val duration: Int? = null
    val performer: String? = null
    val title: String? = null

    fun build() = InputMediaAudio(file, thumbnail, caption, parseMode, captionEntities,
        duration, performer, title)
}