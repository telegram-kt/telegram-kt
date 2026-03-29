package io.telegramkt.model.media.input.builder

import io.telegramkt.model.ParseMode
import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.media.input.InputMediaPhoto
import io.telegramkt.model.message.entity.MessageEntity
import kotlinx.serialization.SerialName

class InputMediaPhotoBuilder(private val file: InputFile) {
    val caption: String? = null
    val parseMode: ParseMode? = null
    val captionEntities: List<MessageEntity>? = null
    val showCaptionAboveMedia: Boolean? = null
    val hasSpoiler: Boolean? = null

    fun build() = InputMediaPhoto(file, caption, parseMode, captionEntities, showCaptionAboveMedia, hasSpoiler)
}