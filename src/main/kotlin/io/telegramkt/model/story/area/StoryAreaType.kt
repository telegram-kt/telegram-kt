package io.telegramkt.model.story.area

import io.telegramkt.model.color.Color
import io.telegramkt.model.location.LocationAddress
import io.telegramkt.model.reaction.ReactionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class StoryAreaType {

    @Serializable
    @SerialName("location")
    data class Location(
        @SerialName("latitude") val latitude: Float,
        @SerialName("longitude") val longitude: Float,
        @SerialName("address") val address: LocationAddress? = null,
    ): StoryAreaType()

    @Serializable
    @SerialName("suggested_reaction")
    data class SuggestedReaction(
        @SerialName("reaction_type") val reactionType: ReactionType,
        @SerialName("is_dark") val isDark: Boolean? = null,
        @SerialName("is_flipped") val isFlipped: Boolean? = null,
    ): StoryAreaType()

    @Serializable
    @SerialName("link")
    data class Link(
        @SerialName("url") val url: String,
    ): StoryAreaType()

    @Serializable
    @SerialName("weather")
    data class Weather(
        @SerialName("temperature") val temperature: Float,
        @SerialName("emoji") val emoji: String,
        @SerialName("background_color") val backgroundColor: Color,
    ): StoryAreaType()

    @Serializable
    @SerialName("unique_gift")
    data class UniqueGift(
        @SerialName("name") val name: String,
    ): StoryAreaType()
}