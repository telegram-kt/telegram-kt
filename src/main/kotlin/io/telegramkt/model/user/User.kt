package io.telegramkt.model.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @SerialName("id") val id: Long,
    @SerialName("is_bot") val isBot: Boolean = false,
    @SerialName("first_name") val firstName: String,
    @SerialName("last_name") val lastName: String? = null,
    @SerialName("username") val username: String? = null,
    @SerialName("language_code") val languageCode: String? = null,
    @SerialName("is_premium") val isPremium: Boolean? = null,
    @SerialName("added_to_attachment_menu") val addedToAttachmentMenu: Boolean? = null,
    @SerialName("can_join_groups") val canJoinGroups: Boolean? = null,
    @SerialName("can_read_all_group_messages") val canReadAllGroupMessages: Boolean? = null,
    @SerialName("supports_inline_queries") val supportsInlineQueries: Boolean? = null,
    @SerialName("can_connect_to_business") val canConnectToBusiness: Boolean? = null,
    @SerialName("has_main_web_app") val hasMainWebApp: Boolean? = null,
) {
    val mention: String get() = "@$username"
    val fullName: String get() = listOfNotNull(firstName, lastName).joinToString(" ")
}
