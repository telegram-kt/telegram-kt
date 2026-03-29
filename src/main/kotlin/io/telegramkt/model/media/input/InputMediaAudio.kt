package io.telegramkt.model.media.input

import io.telegramkt.model.ParseMode
import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.message.entity.MessageEntity
import io.telegramkt.serialization.InputFileSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("audio")
data class InputMediaAudio(
    @Serializable(with = InputFileSerializer::class) override val media: InputFile,
    @Serializable(with = InputFileSerializer::class) val thumbnail: InputFile? = null,
    override val caption: String? = null,
    @SerialName("parse_mode") override val parseMode: ParseMode? = null,
    @SerialName("caption_entities") val captionEntities: List<MessageEntity>? = null,
    val duration: Int? = null,
    val performer: String? = null,
    val title: String? = null,
) : AudioGroup
