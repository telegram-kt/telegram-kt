package io.telegramkt.model.story.area

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StoryAreaPosition(
    @SerialName("x_percentage") val xPercentage: Float,
    @SerialName("y_percentage") val yPercentage: Float,
    @SerialName("width_percentage") val widthPercentage: Float,
    @SerialName("height_percentage") val heightPercentage: Float,
    @SerialName("rotation_angle") val rotationAngle: Float,
    @SerialName("corner_radius_percentage") val cornerRadiusPercentage: Float,
) {
    init {
        require(rotationAngle in 0f..360f) {
            "rotationAngle must be in 0..360, got $rotationAngle"
        }
        require(xPercentage in 0f..100f) { "xPercentage must be in 0..100" }
        require(yPercentage in 0f..100f) { "yPercentage must be in 0..100" }
        require(widthPercentage in 0f..100f) { "widthPercentage must be in 0..100" }
        require(heightPercentage in 0f..100f) { "heightPercentage must be in 0..100" }
        require(cornerRadiusPercentage in 0f..100f) { "cornerRadiusPercentage must be in 0..100" }
    }
}