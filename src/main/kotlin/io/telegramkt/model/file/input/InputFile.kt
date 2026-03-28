package io.telegramkt.model.file.input

import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray

sealed interface InputFile {
    data class StringValue(val value: String) : InputFile

    data class ByteArrayValue(
        val bytes: ByteArray,
        val fileName: String
    ) : InputFile

    data class PathValue(val path: Path) : InputFile {
        fun readBytes(): ByteArray = SystemFileSystem.source(path).buffered().use { it.readByteArray() }
        val fileName: String get() = path.name
    }

    companion object {
        fun fromId(id: String) = StringValue(id)
        fun fromUrl(url: String) = StringValue(url)
        fun fromBytes(bytes: ByteArray, fileName: String) = ByteArrayValue(bytes, fileName)
        fun fromPath(path: Path) = PathValue(path)
        fun fromPath(path: String) = PathValue(Path(path))
    }
}