package io.telegramkt.model.sticker.set

import io.telegramkt.model.photo.PhotoSize
import io.telegramkt.model.sticker.Sticker
import io.telegramkt.model.sticker.enums.StickerType
import io.telegramkt.serialization.sticker.StickerTypeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StickerSet(
    @SerialName("name") val name: String,
    @SerialName("title") val title: String,
    @SerialName("sticker_type")
    @Serializable(with = StickerTypeSerializer::class)
    val stickerType: StickerType,
    @SerialName("stickers") val stickers: List<Sticker>,
    @SerialName("thumbnail") val thumbnail: PhotoSize? = null,
) {
    val addLink = "t.me/addstickers/$name"
}
