package com.rideunify.core.navigation

import com.rideunify.domain.model.TripOffer
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

class WazeDeepLinkBuilder @Inject constructor() {
    fun build(
        offer: TripOffer,
        source: String,
    ): WazeDeepLink? {
        val encodedSource = source.urlEncoded()
        offer.pickupLocation?.let { point ->
            return WazeDeepLink(
                uri = "https://waze.com/ul?ll=${point.latitude},${point.longitude}" +
                    "&navigate=yes&utm_source=$encodedSource",
                queryUsed = offer.pickupAddress,
            )
        }

        val address = offer.pickupAddress?.takeIf { it.isNotBlank() } ?: return null
        return WazeDeepLink(
            uri = "https://waze.com/ul?q=${address.urlEncoded()}&utm_source=$encodedSource",
            queryUsed = address,
        )
    }

    private fun String.urlEncoded(): String =
        URLEncoder.encode(this, StandardCharsets.UTF_8.toString()).replace("+", "%20")
}
