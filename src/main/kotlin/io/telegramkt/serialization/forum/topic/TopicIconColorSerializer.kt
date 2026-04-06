package io.telegramkt.serialization.forum.topic

import io.telegramkt.model.forum.topic.TopicIconColor
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object TopicIconColorSerializer: KSerializer<TopicIconColor> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("TopicIconColor", PrimitiveKind.INT)

    override fun serialize(
        encoder: Encoder,
        value: TopicIconColor
    ) {
        encoder.encodeInt(value.rgb)
    }

    override fun deserialize(decoder: Decoder): TopicIconColor = TopicIconColor.fromInt(decoder.decodeInt())

}