package io.telegramkt.model.sticker.input

import io.telegramkt.model.file.input.InputFile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class StickerFormat {
    abstract val apiValue: String

    @SerialName("static")
    data object Static : StickerFormat() {
        override val apiValue: String = "static"
    }

    @SerialName("animated")
    data object Animated: StickerFormat() {
        override val apiValue: String = "animated"
    }

    @SerialName("video")
    data object Video : StickerFormat() {
        override val apiValue: String = "video"
    }

    companion object {
        fun fromFileName(fileName: String): StickerFormat? {
            return when (fileName.substringAfterLast('.', "").lowercase()) {
                "webp", "png" -> Static
                "tgs" -> Animated
                "webm" -> Video
                else -> null
            }
        }

        fun fromInputFile(inputFile: InputFile): StickerFormat? {
            return when (inputFile) {
                is InputFile.PathValue -> fromFileName(inputFile.fileName)
                is InputFile.ByteArrayValue -> fromFileName(inputFile.fileName)
                is InputFile.StringValue -> {
                    null
                }
            }
        }
    }
}