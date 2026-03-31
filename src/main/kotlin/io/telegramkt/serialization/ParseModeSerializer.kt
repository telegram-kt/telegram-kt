package io.telegramkt.serialization

import io.telegramkt.model.ParseMode
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object ParseModeSerializer : KSerializer<ParseMode> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("ParseMode", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ParseMode) = encoder.encodeString(value.name)

    override fun deserialize(decoder: Decoder): ParseMode = ParseMode.valueOf(decoder.decodeString().uppercase())
}