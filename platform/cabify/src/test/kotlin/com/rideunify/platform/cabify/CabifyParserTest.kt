package com.rideunify.platform.cabify

import com.rideunify.domain.accessibility.ScreenNode
import com.rideunify.domain.accessibility.ScreenSnapshot
import com.rideunify.domain.model.TripPlatform
import com.rideunify.domain.parser.ParseResult
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CabifyParserTest {
    private val parser = CabifyParser()

    @Test
    fun parsesVisibleOfferTextIntoTripOffer() {
        val result = parser.parse(
            ScreenSnapshot(
                packageName = "com.cabify.driver",
                capturedAt = Instant.EPOCH,
                root = ScreenNode(
                    text = null,
                    contentDescription = null,
                    className = "root",
                    isClickable = false,
                    bounds = null,
                    children = listOf(
                        label("Solicitud de viaje Cabify"),
                        label("Av. Santa Fe 2450"),
                        label("ARS 3800"),
                        label("7 min"),
                        label("Aceptar"),
                    ),
                ),
            ),
        )

        assertTrue(result is ParseResult.OfferFound)
        val offer = (result as ParseResult.OfferFound).offer
        assertEquals(TripPlatform.CABIFY, offer.platform)
        assertEquals("Av. Santa Fe 2450", offer.pickupAddress)
        assertEquals(3800.0, offer.estimatedFare?.amount ?: 0.0, 0.0)
        assertEquals(7, offer.estimatedPickupMinutes)
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
