package io.telegramkt.model.forum.topic

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class TopicIconColor(open val rgb: Int) {
    @SerialName("7322096")
    data object Blue : TopicIconColor(0x6FB9F0)

    @SerialName("16766590")
    data object Yellow : TopicIconColor(0xFFD67E)

    @SerialName("13338331")
    data object Purple : TopicIconColor(0xCB86DB)

    @SerialName("9367192")
    data object Green : TopicIconColor(0x8EEE98)

    @SerialName("16749490")
    data object Pink : TopicIconColor(0xFF93B2)

    @SerialName("16478047")
    data object RedOrange : TopicIconColor(0xFB6F5F)

    data class Unknown(override val rgb: Int) : TopicIconColor(rgb)

    companion object {
        fun fromInt(value: Int): TopicIconColor = when(value) {
            7322096 -> Blue
            16766590 -> Yellow
            13338331 -> Purple
            9367192 -> Green
            16749490 -> Pink
            16478047 -> RedOrange
            else -> Unknown(value)
        }
    }
}