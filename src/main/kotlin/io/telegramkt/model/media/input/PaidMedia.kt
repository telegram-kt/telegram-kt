package io.telegramkt.model.media.input

import io.telegramkt.model.photo.PhotoSize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This object describes paid media.
 *
 * See https://core.telegram.org/bots/api#paidmedia
 */
@Serializable
sealed class PaidMedia {

    @Serializable
    @SerialName("photo")
    data class Photo(
        @SerialName("photo") val photo: List<PhotoSize>,
    ) : PaidMedia()

    @Serializable
    @SerialName("video")
    data class Video(
        @SerialName("video") val video: io.telegramkt.model.video.Video,
    ) : PaidMedia()

    @Serializable
    @SerialName("preview")
    data class Preview(
        @SerialName("width") val width: Int? = null,
        @SerialName("height") val height: Int? = null,
        @SerialName("duration") val duration: Int? = null,
    ) : PaidMedia()
}