package io.telegramkt.model.forum

import io.telegramkt.model.forum.topic.TopicIconColor
import io.telegramkt.serialization.forum.topic.TopicIconColorSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForumTopic(
    @SerialName("message_thread_id") val messageThreadId: Int,
    @SerialName("name") val name: String,
    @SerialName("icon_color")
    @Serializable(with = TopicIconColorSerializer::class)
    val iconColor: TopicIconColor,
    @SerialName("icon_custom_emoji_id") val iconCustomEmojiId: String? = null,
    @SerialName("is_name_implicit") val isNameImplicit: Boolean? = null,
)
