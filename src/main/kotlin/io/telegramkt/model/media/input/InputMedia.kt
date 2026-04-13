package io.telegramkt.model.media.input

import io.telegramkt.model.ParseMode
import io.telegramkt.model.file.input.InputFile
import kotlinx.serialization.Serializable

@Serializable
sealed interface InputMedia {
    val media: InputFile
    val caption: String?
    val parseMode: ParseMode?
}