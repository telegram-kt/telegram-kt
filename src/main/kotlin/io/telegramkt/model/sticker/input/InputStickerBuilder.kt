package io.telegramkt.model.sticker.input

import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.mask.MaskPosition

class InputStickerBuilder {
    private var sticker: InputFile? = null
    private var format: StickerFormat? = null
    private var emojis: MutableList<String> = mutableListOf()
    private var keywords: List<String>? = null
    private var maskPosition: MaskPosition? = null

    fun sticker(sticker: InputFile) = apply { this.sticker = sticker }

    fun static() = apply { format = StickerFormat.Static }
    fun animated() = apply { format = StickerFormat.Animated }
    fun video() = apply { format = StickerFormat.Video }

    fun autoDetect() = apply {
        format = sticker?.let { StickerFormat.fromInputFile(it) }
            ?: error("Cannot auto-detect format from StringValue (URL/file_id). Please specify format explicitly.")
    }

    fun emoji(vararg emojis: String) = apply { this.emojis.addAll(emojis) }
    fun keywords(keywords: List<String>) = apply { this.keywords = keywords }
    fun maskPosition(position: MaskPosition) = apply { this.maskPosition = position }

    fun build(): InputSticker {
        requireNotNull(sticker) { "Sticker is required" }
        requireNotNull(format) { "Format is required. Use .static()/.animated()/.video() or .autoDetect()" }
        require(emojis.isNotEmpty()) { "At least one emoji is required" }

        return InputSticker(sticker!!, format!!, emojis.toList(), maskPosition, keywords)
    }
}

fun inputSticker(
    sticker: InputFile,
    vararg emojis: String,
    format: StickerFormat? = null,
    block: InputStickerBuilder.() -> Unit = {}
): InputSticker {
    return InputStickerBuilder().apply {
        sticker(sticker)
        if (format != null) {
            when (format) {
                StickerFormat.Static -> static()
                StickerFormat.Animated -> animated()
                StickerFormat.Video -> video()
            }
        } else {
            autoDetect()
        }
        emoji(*emojis)
        block()
    }.build()
}

fun InputFile.asStaticSticker(vararg emojis: String) =
    inputSticker(this, *emojis, format = StickerFormat.Static)

fun InputFile.asAnimatedSticker(vararg emojis: String) =
    inputSticker(this, *emojis, format = StickerFormat.Animated)

fun InputFile.asVideoSticker(vararg emojis: String) =
    inputSticker(this, *emojis, format = StickerFormat.Video)

fun InputFile.asAutoSticker(vararg emojis: String) =
    inputSticker(this, *emojis)