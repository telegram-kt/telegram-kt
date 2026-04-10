package io.telegramkt.model.story

import io.telegramkt.model.file.input.InputFile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class InputStoryContent {

    @Serializable
    @SerialName("photo")
    data class Photo(
        @SerialName("photo") val photo: InputFile,
    ): InputStoryContent()

    @Serializable
    @SerialName("video")
    data class Video(
        @SerialName("video") val video: InputFile,
        @SerialName("duration") val duration: Float? = null,
        @SerialName("cover_frame_timestamp") val coverFrameTimestamp: Float? = null,
        @SerialName("is_animation") val isAnimation: Boolean? = null,
    ): InputStoryContent()
}