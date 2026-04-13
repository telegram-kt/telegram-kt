package io.telegramkt.serialization.duration

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object DurationSecondsSerializer: KSerializer<Duration> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("DurationSeconds", PrimitiveKind.INT)

    override fun serialize(encoder: Encoder, value: Duration) {
        encoder.encodeInt(value.inWholeSeconds.toInt())
    }

    override fun deserialize(decoder: Decoder): Duration = decoder.decodeInt().seconds

}