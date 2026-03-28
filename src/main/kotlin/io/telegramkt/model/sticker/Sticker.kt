package io.telegramkt.model.sticker

import io.telegramkt.model.file.File
import io.telegramkt.model.mask.MaskPosition
import io.telegramkt.model.photo.PhotoSize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sticker(
    @SerialName("file_id") val fileId: String,
    @SerialName("file_unique_id") val fileUniqueId: String,
    @SerialName("type") val type: String,
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int,
    @SerialName("is_animated") val isAnimated: Boolean,
    @SerialName("is_video") val isVideo: Boolean,
    @SerialName("thumbnail") val thumbnail: PhotoSize? = null,
    @SerialName("emoji") val emoji: String? = null,
    @SerialName("set_name") val setName: String? = null,
    @SerialName("premium_animation") val premiumAnimation: File? = null,
    @SerialName("mask_position") val maskPosition: MaskPosition? = null,
    @SerialName("custom_emoji_id") val customEmojiId: String? = null,
    @SerialName("needs_repainting") val needsRepainting: Boolean? = null,
    @SerialName("file_size") val fileSize: Int? = null,
)
