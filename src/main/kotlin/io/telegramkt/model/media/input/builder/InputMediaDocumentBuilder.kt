package io.telegramkt.model.media.input.builder

import io.telegramkt.model.ParseMode
import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.media.input.InputMediaDocument
import io.telegramkt.model.message.entity.MessageEntity

class InputMediaDocumentBuilder(private val file: InputFile) {
    val thumbnail: InputFile? = null
    val caption: String? = null
    val parseMode: ParseMode? = null
    val captionEntities: List<MessageEntity>? = null
    val disableContentTypeDetection: Boolean? = null

    fun build() = InputMediaDocument(file, thumbnail, caption, parseMode, captionEntities,
        disableContentTypeDetection)
}