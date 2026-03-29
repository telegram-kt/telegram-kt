package io.telegramkt.model.media.input

import io.telegramkt.model.ParseMode
import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.message.entity.MessageEntity
import io.telegramkt.serialization.InputFileSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("video")
data class InputMediaVideo(
    @Serializable(with = InputFileSerializer::class) override val media: InputFile,
    @Serializable(with = InputFileSerializer::class) val thumbnail: InputFile? = null,
    @Serializable(with = InputFileSerializer::class) val cover: InputFile? = null,
    @SerialName("start_timestamp") val startTimestamp: Int? = null,
    override val caption: String? = null,
    @SerialName("parse_mode") override val parseMode: ParseMode? = null,
    @SerialName("caption_entities") val captionEntities: List<MessageEntity>? = null,
    @SerialName("show_caption_above_media") val showCaptionAboveMedia: Boolean? = null,
    val width: Int? = null,
    val height: Int? = null,
    val duration: Int? = null,
    @SerialName("supports_streaming") val supportsStreaming: Boolean? = null,
    @SerialName("has_spoiler") val hasSpoiler: Boolean? = null,
): PhotoVideoGroup