package io.telegramkt.model.game

import io.telegramkt.model.animation.Animation
import io.telegramkt.model.message.entity.MessageEntity
import io.telegramkt.model.photo.PhotoSize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Game(
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("photo") val photo: List<PhotoSize>,
    @SerialName("text") val text: String? = null,
    @SerialName("text_entities") val textEntities: List<MessageEntity>? = null,
    @SerialName("animation") val animation: Animation? = null,
)
