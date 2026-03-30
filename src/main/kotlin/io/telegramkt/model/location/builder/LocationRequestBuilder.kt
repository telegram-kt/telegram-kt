package io.telegramkt.model.location.builder

import io.telegramkt.model.location.Location

class LocationRequestBuilder(val latitude: Float, var longitude: Float) {
    var horizontalAccuracy: Float? = null
    var livePeriod: Int? = null
    var heading: Int? = null
    var proximityAlertRadius: Int? = null

    fun build() = Location(latitude, longitude, horizontalAccuracy, livePeriod, heading, proximityAlertRadius)
}
