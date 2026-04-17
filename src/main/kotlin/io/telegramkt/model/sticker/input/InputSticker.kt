package io.telegramkt.model.sticker.input

import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.mask.MaskPosition
import io.telegramkt.serialization.InputFileSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InputSticker(
    @SerialName("sticker")
    @Serializable(with = InputFileSerializer::class)
    val sticker: InputFile,
    @SerialName("sticker_format") val format: StickerFormat,
    @SerialName("emoji_list") val emojiList: List<String>,
    @SerialName("mask_position") val maskPosition: MaskPosition? = null,
    @SerialName("keywords") val keywords: List<String>? = null,
) {
    init {
        require(emojiList.isNotEmpty()) { "At least one emoji is required" }
    }
}
