package io.telegramkt.serialization.sticker

import io.telegramkt.model.sticker.enums.StickerType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object StickerTypeSerializer : KSerializer<StickerType> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("StickerSet", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: StickerType) = encoder.encodeString(value.name)

    override fun deserialize(decoder: Decoder): StickerType
        = StickerType.valueOf(decoder.decodeString().uppercase())
}