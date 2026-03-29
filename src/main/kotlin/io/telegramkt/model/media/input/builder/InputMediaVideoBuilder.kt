package io.telegramkt.model.media.input.builder

import io.telegramkt.model.ParseMode
import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.media.input.InputMediaVideo
import io.telegramkt.model.message.entity.MessageEntity

class InputMediaVideoBuilder(private val file: InputFile) {
    val thumbnail: InputFile? = null
    val cover: InputFile? = null
    val startTimestamp: Int? = null
    val caption: String? = null
    val parseMode: ParseMode? = null
    val captionEntities: List<MessageEntity>? = null
    val showCaptionAboveMedia: Boolean? = null
    val width: Int? = null
    val height: Int? = null
    val duration: Int? = null
    val supportsStreaming: Boolean? = null
    val hasSpoiler: Boolean? = null


    fun build() = InputMediaVideo(file, thumbnail, cover, startTimestamp, caption,
        parseMode, captionEntities, showCaptionAboveMedia, width, height, duration, supportsStreaming, hasSpoiler)
}