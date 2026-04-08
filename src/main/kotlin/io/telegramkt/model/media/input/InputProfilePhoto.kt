package io.telegramkt.model.media.input

import io.telegramkt.model.file.input.InputFile
import io.telegramkt.serialization.InputFileSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class InputProfilePhoto {
    @Serializable
    @SerialName("static")
    data class Static(
        @SerialName("photo") val photo: InputFile
    ): InputProfilePhoto()

    @Serializable
    @SerialName("animated")
    data class Animated(
        @SerialName("animation") val animation: InputFile,
        @SerialName("main_frame_timestamp") val mainFrameTimestamp: Float? = null
    ) : InputProfilePhoto()
}