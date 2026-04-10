package io.telegramkt.serialization.story

import io.telegramkt.model.story.StoryActivePeriod
import io.telegramkt.model.story.StoryActivePeriod.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object StoryActivePeriodSerializer : KSerializer<StoryActivePeriod> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StoryActivePeriod", PrimitiveKind.LONG)

    override fun serialize(
        encoder: Encoder,
        value: StoryActivePeriod
    ) {
        encoder.encodeLong(value.duration.inWholeSeconds)
    }

    override fun deserialize(decoder: Decoder): StoryActivePeriod {
        val seconds = decoder.decodeLong()
        return fromSeconds(seconds)
    }

    private fun fromSeconds(seconds: Long): StoryActivePeriod = when (seconds) {
        6 * 3600L -> SIX_HOURS
        12 * 3600L -> TWELVE_HOURS
        86400L -> ONE_DAY
        2 * 86400L -> TWO_DAYS
        else -> throw IllegalArgumentException("Invalid active period: $seconds seconds")
    }
}