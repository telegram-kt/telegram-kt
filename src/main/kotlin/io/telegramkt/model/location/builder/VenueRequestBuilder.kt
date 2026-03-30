package io.telegramkt.model.location.builder

import io.telegramkt.model.location.Location
import io.telegramkt.model.location.Venue

class VenueRequestBuilder(var location: Location, var title: String, var address: String) {
    var foursquareId: String? = null
    var foursquareType: String? = null
    var googlePlaceId: String? = null
    var googlePlaceType: String? = null

    fun build() = Venue(location, title, address, foursquareId, foursquareType, googlePlaceId, googlePlaceType)
}