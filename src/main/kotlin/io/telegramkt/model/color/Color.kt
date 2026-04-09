package io.telegramkt.model.color

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Color(val rgb: Int) {
    companion object {
        fun fromRGB(r: Int, g: Int, b: Int): Color {
            require(r in 0..255 && g in 0..255 && b in 0..255) {
                "RGB values must be in 0..255."
            }
            return Color((r shl 16) or (g shl 8) or b)
        }

        fun fromHex(hex: String): Color {
            val clean = hex.removePrefix("#")
            return Color(clean.toInt(16))
        }

        val RED = Color(0xFF0000)
        val GREEN = Color(0x00FF00)
        val BLUE = Color(0x0000FF)
        val WHITE = Color(0xFFFFFF)
        val BLACK = Color(0x000000)
        val TRANSPARENT = Color(0)
    }

    val r: Int get() = (rgb shr 16) and 0xFF
    val g: Int get() = (rgb shr 8) and 0xFF
    val b: Int get() = rgb and 0xFF

    fun toHex(): String = "#%06X".format(rgb)
    fun toRGBString(): String = "rgb($r, $g, $b)"

    override fun toString(): String = toHex()
}