package io.telegramkt.serialization

import io.telegramkt.model.file.input.InputFile
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object InputFileSerializer : KSerializer<InputFile> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("InputFile", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: InputFile
    ) {
        when (value) {
            is InputFile.StringValue -> encoder.encodeString(value.value)
            is InputFile.ByteArrayValue -> {
                encoder.encodeString("attach://${value.fileName}")
            }
            is InputFile.PathValue -> {
                encoder.encodeString("attach://${value.fileName}")
            }
        }
    }

    override fun deserialize(decoder: Decoder): InputFile {
        return InputFile.fromId(decoder.decodeString())
    }
}