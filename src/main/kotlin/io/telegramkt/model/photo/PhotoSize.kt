package io.telegramkt.model.photo

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoSize(
    @SerialName("file_id") val fileId: String,
    @SerialName("file_unique_id") val fileUniqueId: String,
    @SerialName("width") val width: Int,
    @SerialName("height") val height: Int,
    @SerialName("file_size") val fileSize: Int,
)

/**
 * Returns the largest available photo size by resolution (width × height).
 *
 * Useful when you need the highest quality image, e.g., for saving to disk
 * or displaying in full-screen mode.
 *
 * ### Example:
 * ```
 * val photo = message.photo?.largest()
 * bot.downloadFile(photo?.fileId)
 * ```
 *
 * @return The largest [PhotoSize], or `null` if the list is empty
 * @see smallest For the opposite use case
 * @see bestFit When you need to respect display constraints
 */
fun List<PhotoSize>.largest(): PhotoSize? =
    maxByOrNull { it.width * it.height }

/**
 * Returns the smallest available photo size by resolution (width × height).
 *
 * Useful for thumbnails, previews, or when bandwidth is limited.
 *
 * ### Example:
 * ```
 * // Load tiny preview first, then lazy-load full image
 * val thumbnail = message.photo?.smallest()
 * val fullImage = message.photo?.largest()
 * ```
 *
 * @return The smallest [PhotoSize], or `null` if the list is empty
 * @see largest For the highest quality version
 */
fun List<PhotoSize>.smallest(): PhotoSize? =
    minByOrNull { it.width * it.height }

/**
 * Returns the largest photo that fits within the given dimensions.
 *
 * Perfect for displaying images in UI components with fixed size constraints
 * without wasting bandwidth on unnecessarily large images.
 *
 * ### Example:
 * ```
 * // Get best image for 1920×1080 display
 * val displaySize = message.photo?.bestFit(maxWidth = 1920, maxHeight = 1080)
 *
 * // Or constrain by memory (e.g., RecyclerView thumbnails)
 * val thumbnail = message.photo?.bestFit(maxWidth = 300, maxHeight = 300)
 * ```
 *
 * @param maxWidth Maximum allowed width in pixels (default: unlimited)
 * @param maxHeight Maximum allowed height in pixels (default: unlimited)
 * @return The largest fitting [PhotoSize], or `null` if none match constraints
 * @see closestTo When you need exact dimensions rather than "fit within"
 */
fun List<PhotoSize>.bestFit(maxWidth: Int = Int.MAX_VALUE, maxHeight: Int = Int.MAX_VALUE): PhotoSize? =
    filter { it.width <= maxWidth && it.height <= maxHeight }
        .maxByOrNull { it.width * it.height }

/**
 * Returns the photo size closest to the target resolution.
 *
 * Minimizes the difference in total pixel count (area), useful when you need
 * a specific resolution for processing or display purposes.
 *
 * ### Example:
 * ```
 * // Need exactly 800×600 for your image processing pipeline?
 * val optimal = message.photo?.closestTo(targetWidth = 800, targetHeight = 600)
 * ```
 *
 * @param targetWidth Desired width in pixels
 * @param targetHeight Desired height in pixels
 * @return The [PhotoSize] with resolution closest to target, or `null` if list is empty
 * @see bestFit When you need "at most" rather than "closest to"
 */
fun List<PhotoSize>.closestTo(targetWidth: Int, targetHeight: Int): PhotoSize? {
    val targetArea = targetWidth * targetHeight
    return minByOrNull {
        kotlin.math.abs(it.width * it.height - targetArea)
    }
}
