package io.telegramkt.model.audio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Voice(
    @SerialName("file_id") val fileId: String,
    @SerialName("file_unique_id") val fileUniqueId: String,
    @SerialName("duration") val duration: Int,
    @SerialName("mime_type") val mimeType: String? = null,
    @SerialName("file_size") val fileSize: Int? = null,
)
