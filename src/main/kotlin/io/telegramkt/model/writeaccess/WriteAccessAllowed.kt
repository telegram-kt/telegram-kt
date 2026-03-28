package io.telegramkt.model.writeaccess

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WriteAccessAllowed(
    @SerialName("from_request") val fromRequest: Boolean? = null,
    @SerialName("web_app_name") val webAppName: String? = null,
    @SerialName("from_attachment_menu") val fromAttachmentMenu: Boolean? = null,
)
