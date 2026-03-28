package io.telegramkt.model.forum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForumTopicCreated(
    @SerialName("name") val name: String,
    @SerialName("icon_color") val iconColor: Int,
    @SerialName("icon_custom_emoji_id") val iconCustomEmojiId: String? = null,
    @SerialName("is_name_implicit") val isNameImplicit: Boolean? = null,
)
