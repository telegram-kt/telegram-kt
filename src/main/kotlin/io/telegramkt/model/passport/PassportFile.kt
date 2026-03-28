package io.telegramkt.model.passport

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PassportFile(
    @SerialName("file_id") val fileId: String,
    @SerialName("file_unique_id") val fileUniqueId: String,
    @SerialName("file_size") val fileSize: Int,
    @SerialName("file_date") val fileDate: Int,
)
