package com.rideunify.platform.uber

import com.rideunify.domain.accessibility.ScreenNode
import com.rideunify.domain.accessibility.ScreenSnapshot
import com.rideunify.domain.model.TripPlatform
import com.rideunify.domain.parser.ParseResult
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UberParserTest {
    private val parser = UberParser()

    @Test
    fun parsesVisibleOfferTextIntoTripOffer() {
        val result = parser.parse(
            ScreenSnapshot(
                packageName = "com.ubercab.driver",
                capturedAt = Instant.EPOCH,
                root = ScreenNode(
                    text = null,
                    contentDescription = null,
                    className = "root",
                    isClickable = false,
                    bounds = null,
                    children = listOf(
                        label("Nuevo viaje Uber"),
                        label("Av. Corrientes 1234"),
                        label("$ 4200"),
                        label("5 min"),
                        label("4.2 km"),
                        label("Aceptar"),
                    ),
                ),
            ),
        )

        assertTrue(result is ParseResult.OfferFound)
        val offer = (result as ParseResult.OfferFound).offer
        assertEquals(TripPlatform.UBER, offer.platform)
        assertEquals("Av. Corrientes 1234", offer.pickupAddress)
        assertEquals(4200.0, offer.estimatedFare?.amount ?: 0.0, 0.0)
        assertEquals(5, offer.estimatedPickupMinutes)
    }

    private fun label(text: String): ScreenNode =
        ScreenNode(
            text = text,
            contentDescription = null,
            className = "android.widget.TextView",
            isClickable = false,
            bounds = null,
        )
}
