package com.rideunify.core.navigation

import com.rideunify.domain.model.GeoPoint
import com.rideunify.domain.model.TripOffer
import com.rideunify.domain.model.TripPlatform
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WazeDeepLinkBuilderTest {
    private val builder = WazeDeepLinkBuilder()

    @Test
    fun buildsCoordinateNavigationWhenPickupLocationExists() {
        val link = builder.build(
            offer = baseOffer().copy(
                pickupAddress = "Av. Corrientes 1234",
                pickupLocation = GeoPoint(-34.6037, -58.3816),
            ),
            source = "com.rideunify.app",
        )

        assertEquals(
            "https://waze.com/ul?ll=-34.6037,-58.3816&navigate=yes&utm_source=com.rideunify.app",
            link?.uri,
        )
        assertEquals("Av. Corrientes 1234", link?.queryUsed)
    }

    @Test
    fun buildsQueryNavigationWhenOnlyPickupAddressExists() {
        val link = builder.build(
            offer = baseOffer().copy(pickupAddress = "Av. Santa Fe 2450, CABA"),
            source = "com.rideunify.app",
        )

        assertEquals(
            "https://waze.com/ul?q=Av.%20Santa%20Fe%202450%2C%20CABA&utm_source=com.rideunify.app",
            link?.uri,
        )
        assertEquals("Av. Santa Fe 2450, CABA", link?.queryUsed)
    }

    @Test
    fun returnsNullWhenPickupIsMissing() {
        assertNull(builder.build(baseOffer(), "com.rideunify.app"))
    }

    private fun baseOffer(): TripOffer =
        TripOffer(
            id = "test",
            platform = TripPlatform.UBER,
            pickupAddress = null,
            pickupLocation = null,
            destinationAddress = null,
            estimatedFare = null,
            estimatedDistance = null,
            estimatedPickupMinutes = null,
            passengerRating = null,
            confidence = 0.8f,
            receivedAt = Instant.EPOCH,
            expiresAt = null,
        )
}
