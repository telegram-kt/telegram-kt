package io.telegramkt.serialization

import io.telegramkt.model.date.WeekMinute
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object WeekMinuteSerializer : KSerializer<WeekMinute> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("WeekMinute", PrimitiveKind.INT)

    override fun serialize(
        encoder: Encoder,
        value: WeekMinute
    ) {
        encoder.encodeInt(value.value)
    }

    override fun deserialize(decoder: Decoder): WeekMinute = WeekMinute(decoder.decodeInt())
}