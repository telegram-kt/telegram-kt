package io.telegramkt.model.media.paid

import io.telegramkt.model.file.input.InputFile
import io.telegramkt.serialization.InputFileSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
@JsonClassDiscriminator("type")
sealed class InputPaidMedia {
    @Serializable(with = InputFileSerializer::class)
    abstract val media: InputFile

    @Serializable
    @SerialName("photo")
    data class Photo(
        @SerialName("media") override val media: InputFile,
    ): InputPaidMedia()

    @Serializable
    @SerialName("video")
    data class Video(
        @SerialName("media") override val media: InputFile,
        @Serializable(with = InputFileSerializer::class) @SerialName("thumbnail") val thumbnail: InputFile? = null,
        @SerialName("cover") val cover: String? = null,
        @SerialName("start_timestamp") val startTimestamp: Int? = null,
        @SerialName("width") val width: Int? = null,
        @SerialName("height") val height: Int? = null,
        @SerialName("duration") val duration: Int? = null,
        @SerialName("supports_streaming") val supportsStreaming: Boolean? = null,
    ): InputPaidMedia()
}