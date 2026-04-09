package io.telegramkt.client

import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.fromFilePath
import io.telegramkt.model.chat.administrator.ChatAdministratorRights
import io.telegramkt.model.file.input.InputFile
import io.telegramkt.model.forum.topic.TopicIconColor
import io.telegramkt.model.media.input.AlbumableMedia
import io.telegramkt.model.media.input.InputMediaAudio
import io.telegramkt.model.media.input.InputMediaDocument
import io.telegramkt.model.media.input.InputMediaPhoto
import io.telegramkt.model.media.input.InputMediaVideo
import io.telegramkt.model.media.input.InputProfilePhoto
import io.telegramkt.model.media.paid.InputPaidMedia
import io.telegramkt.model.menu.button.MenuButton
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.put
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Instant

internal fun Map<String, Any?>.hasBinaryFiles(): Boolean =
    values.any { value ->
        when (value) {
            is InputFile -> value !is InputFile.StringValue
            is InputProfilePhoto -> {
                val file = when(value) {
                    is InputProfilePhoto.Static -> value.photo
                    is InputProfilePhoto.Animated -> value.animation
                }
                file !is InputFile.StringValue
            }
            is List<*> -> value.any { item ->
                (item is AlbumableMedia && item.media !is InputFile.StringValue) ||
                        (item is InputPaidMedia && item.media !is InputFile.StringValue)
            }
            else -> false
        }
    }

internal fun Json.buildJsonBody(params: Map<String, Any?>): JsonObject =
    buildJsonObject {
        params.forEach { (key, value) ->
            if (value == null) return@forEach

            val element = when (value) {
                is String -> JsonPrimitive(value)
                is Number -> JsonPrimitive(value)
                is Boolean -> JsonPrimitive(value)
                is InputFile.StringValue -> JsonPrimitive(value.value)
                is InputProfilePhoto -> encodeToJsonElement(value)
                is List<*> -> {
                    when {
                        value.filterIsInstance<AlbumableMedia>().isNotEmpty() ->
                            encodeToJsonElement(value.filterIsInstance<AlbumableMedia>())
                        value.filterIsInstance<InputPaidMedia>().isNotEmpty() ->
                            encodeToJsonElement(value.filterIsInstance<InputPaidMedia>())
                        else -> encodeToJsonElement(value.filterIsInstance<String>())
                    }
                }
                is Enum<*> -> {
                    encodeToJsonElement(value.toString().lowercase())
                }
                is Instant -> JsonPrimitive(value.epochSeconds)
                is Duration -> JsonPrimitive(value.inWholeSeconds)
                is TopicIconColor -> JsonPrimitive(value.rgb)
                is MenuButton -> encodeToJsonElement(value)
                is ChatAdministratorRights -> encodeToJsonElement(value)
                else -> encodeToJsonElement(value)
            }
            put(key, element)
        }
    }

internal fun Json.buildMultipartBody(params: Map<String, Any?>): MultiPartFormDataContent =
    MultiPartFormDataContent(formData {
        params.forEach { (key, value) ->
            appendParameter(this@buildMultipartBody, key, value)
        }
    })

private fun FormBuilder.appendParameter(json: Json, key: String, value: Any?) {
    when (value) {
        null -> Unit
        is InputFile.PathValue -> appendBinary(key, value.readBytes(), value.fileName)
        is InputFile.ByteArrayValue -> appendBinary(key, value.bytes, value.fileName)
        is List<*> -> appendList(json, key, value)
        is InputFile.StringValue -> append(key, value.value)
        is InputProfilePhoto -> appendInputProfilePhoto(json, key, value)
        is String -> append(key, value)
        is Number -> append(key, value.toString())
        is Boolean -> append(key, value.toString())
        else -> append(key, json.encodeToString(value))
    }
}

private fun FormBuilder.appendInputProfilePhoto(json: Json, key: String, photo: InputProfilePhoto) {
    val (file, type) = when (photo) {
        is InputProfilePhoto.Static -> photo.photo to "static"
        is InputProfilePhoto.Animated -> photo.animation to "animated"
    }

    val attachName = "profile_photo_${Random.nextInt(10000)}"

    when (file) {
        is InputFile.PathValue -> appendBinary(attachName, file.readBytes(), file.fileName)
        is InputFile.ByteArrayValue -> appendBinary(attachName, file.bytes, file.fileName)
        is InputFile.StringValue -> {
            val photoJson = buildJsonObject {
                put("type", type)
                put("photo", file.value)
            }
            append(key, json.encodeToString(photoJson))
            return
        }
    }

    val photoJson = buildJsonObject {
        put("type", type)
        when (photo) {
            is InputProfilePhoto.Static -> put("photo", "attach://$attachName")
            is InputProfilePhoto.Animated -> {
                put("animation", "attach://$attachName")
                photo.mainFrameTimestamp?.let { put("main_frame_timestamp", it.toDouble()) }
            }
        }
    }

    append(key, json.encodeToString(photoJson))
}

private fun FormBuilder.appendBinary(key: String, bytes: ByteArray, fileName: String) {
    append(key, bytes, Headers.build {
        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")

        val contentType = ContentType.fromFilePath(fileName).firstOrNull()
            ?: ContentType.Application.OctetStream
        append(HttpHeaders.ContentType, contentType.toString())
    })
}

private fun FormBuilder.appendList(json: Json, key: String, list: List<*>) {
    if (list.isEmpty()) {
        append(key, "[]")
        return
    }

    val first = list.firstOrNull()

    if (first !is InputPaidMedia && first !is AlbumableMedia) {
        append(key, json.encodeToString(list.filterNotNull()))
        return
    }

    val processed = list.mapIndexed { index, item ->
        processMediaItem(item, index) ?: item
    }

    val jsonArray = when {
        processed.all { it is AlbumableMedia } ->
            json.encodeToString(processed.filterIsInstance<AlbumableMedia>())
        processed.all { it is InputPaidMedia } ->
            json.encodeToString(processed.filterIsInstance<InputPaidMedia>())
        else -> json.encodeToString(processed.map { it.toString() })
    }

    append(key, jsonArray)
}

private fun FormBuilder.processMediaItem(item: Any?, index: Int): Any? {
    return when (item) {
        is InputPaidMedia -> processWithFile(item, item.media, "paid", index) { newFile ->
            when (item) {
                is InputPaidMedia.Photo -> item.copy(media = newFile)
                is InputPaidMedia.Video -> item.copy(media = newFile)
            }
        }
        is AlbumableMedia -> processWithFile(item, item.media, "album", index) { newFile ->
            when (item) {
                is InputMediaPhoto -> item.copy(media = newFile)
                is InputMediaVideo -> item.copy(media = newFile)
                is InputMediaAudio -> item.copy(media = newFile)
                is InputMediaDocument -> item.copy(media = newFile)
            }
        }
        else -> null
    }
}

private inline fun <T> FormBuilder.processWithFile(
    item: T,
    file: InputFile,
    prefix: String,
    index: Int,
    createCopy: (InputFile.StringValue) -> T
): T? {
    if (file is InputFile.StringValue) return null

    val (bytes, fileName) = when (file) {
        is InputFile.ByteArrayValue -> file.bytes to file.fileName
        is InputFile.PathValue -> file.readBytes() to file.fileName
    }

    val attachName = "${prefix}_file_${index}_${Random.nextInt(10000)}"
    appendBinary(attachName, bytes, fileName)

    return createCopy(InputFile.fromId("attach://$attachName"))
}