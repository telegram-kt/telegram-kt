package io.telegramkt.client

import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.telegramkt.model.file.input.InputFile
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.encodeToJsonElement

internal fun Map<String, Any?>.hasBinaryFiles(): Boolean =
    values.any { it is InputFile && it !is InputFile.StringValue }

internal fun Json.buildJsonBody(params: Map<String, Any?>): JsonObject =
    buildJsonObject {
        params.forEach { (key, value) ->
            if (value == null) return@forEach

            val element = when (value) {
                is String -> JsonPrimitive(value)
                is Number -> JsonPrimitive(value)
                is Boolean -> JsonPrimitive(value)
                is InputFile.StringValue -> JsonPrimitive(value.value)
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
        null -> {}
        is InputFile.PathValue -> {
            val bytes = value.readBytes()
            append(key, bytes, Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"${value.fileName}\"")
            })
        }
        is InputFile.ByteArrayValue -> {
            append(key, value.bytes, Headers.build {
                append(HttpHeaders.ContentDisposition, "filename=\"${value.fileName}\"")
            })
        }
        is InputFile.StringValue -> append(key, value.value)
        is String -> append(key, value)
        is Number -> append(key, value.toString())
        is Boolean -> append(key, value.toString())
        else -> append(key, json.encodeToString(value))
    }
}