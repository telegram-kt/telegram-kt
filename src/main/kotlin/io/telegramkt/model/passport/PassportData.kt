package io.telegramkt.model.passport

import io.telegramkt.model.credentials.EncryptedCredentials
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PassportData(
    @SerialName("data") val data: List<EncryptedPassportElement>,
    @SerialName("credentials") val credentials: EncryptedCredentials,
)
