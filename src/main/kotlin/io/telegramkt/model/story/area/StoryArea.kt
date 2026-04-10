package io.telegramkt.model.story.area

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoryArea(
    @SerialName("position") val position: StoryAreaPosition,
    @SerialName("type") val type: StoryAreaType,
)