package io.telegramkt.model.forum

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForumTopicEdited(
    @SerialName("name") val name: String? = null,
    @SerialName("icon_custom_emoji_id") val iconCustomEmojiId: String? = null,
)
