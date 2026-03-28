package io.telegramkt.api

import io.telegramkt.exception.TelegramApiException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TelegramResponse<T>(
    @SerialName("ok") val ok: Boolean,
    @SerialName("result") val result: T? = null,
    @SerialName("error_code") val errorCode: Int? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("parameters") val parameters: ResponseParameters? = null,
) {
    fun requireSuccess(): TelegramResponse<T> {
        if (!ok) {
            throw TelegramApiException(
                errorCode = errorCode ?: -1,
                message = description ?: "Unknown error",
                parameters = parameters
            )
        }
        return this
    }

    fun requireResult(): T = result ?: throw TelegramApiException(
        errorCode = -1,
        message = "Result is null despite ok=true"
    )
}
