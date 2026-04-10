package io.telegramkt.model.gift

import io.telegramkt.model.color.Color
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UniqueGiftColors(
    @SerialName("model_custom_emoji_id") val modelCustomEmojiId: String,
    @SerialName("symbol_custom_emoji_id") val symbolCustomEmojiId: String,
    @SerialName("light_theme_main_color") val lightThemeMainColor: Color,
    @SerialName("light_theme_other_colors") val lightThemeOtherColors: List<Color>,
    @SerialName("dark_theme_main_color") val darkThemeMainColor: Color,
    @SerialName("dark_theme_other_colors") val darkThemeOtherColors: List<Color>,
)
