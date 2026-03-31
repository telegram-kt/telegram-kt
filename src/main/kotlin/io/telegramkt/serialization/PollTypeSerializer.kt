package io.telegramkt.serialization

import io.telegramkt.model.poll.PollType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object PollTypeSerializer : KSerializer<PollType> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("PollType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: PollType) = encoder.encodeString(value.name)

    override fun deserialize(decoder: Decoder): PollType = PollType.valueOf(decoder.decodeString().uppercase())
}