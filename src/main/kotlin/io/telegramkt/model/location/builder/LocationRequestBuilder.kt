package io.telegramkt.model.location.builder

import io.telegramkt.model.location.Location
import kotlin.time.Duration

class LocationRequestBuilder(val latitude: Float, var longitude: Float) {
    var horizontalAccuracy: Float? = null
    var livePeriod: Duration? = null
    var heading: Int? = null
    var proximityAlertRadius: Int? = null

    fun build() = Location(
        latitude = latitude,
        longitude = longitude,
        horizontalAccuracy = horizontalAccuracy,
        livePeriod = livePeriod,
        heading = heading,
        proximityAlertRadius = proximityAlertRadius
    )
}
