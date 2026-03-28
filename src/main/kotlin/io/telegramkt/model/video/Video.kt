package io.telegramkt.model.video

import io.telegramkt.model.photo.PhotoSize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Video(
    @SerialName("file_id") val fileId: String,
    @SerialName("file_unique_id") val fileUniqueId: String,
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int,
    @SerialName("duration") val duration: Int,
    @SerialName("thumbnail") val thumbnail: PhotoSize? = null,
    @SerialName("cover") val cover: List<PhotoSize>? = null,
    @SerialName("start_timestamp") val startTimestamp: Int? = null,
    @SerialName("qualities") val qualities: List<VideoQuality>? = null,
    @SerialName("file_name") val fileName: String? = null,
    @SerialName("mime_type") val mimeType: String? = null,
    @SerialName("file_size") val fileSize: Int? = null,
)
