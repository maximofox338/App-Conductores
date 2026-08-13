package com.rideunify.domain.filter

import com.rideunify.domain.model.TripOffer
import com.rideunify.domain.model.TripPlatform
import com.rideunify.domain.model.RecommendationType
import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultFilterPolicyTest {
    private val policy = DefaultFilterPolicy()

    @Test
    fun recommendsReviewWhenConfidenceIsLow() {
        val decision = policy.evaluate(baseOffer().copy(confidence = 0.4f))

        assertEquals(RecommendationType.REVIEW, decision.type)
    }

    @Test
    fun recommendsReviewWhenPickupTimeIsHigh() {
        val decision = policy.evaluate(
            baseOffer().copy(
                confidence = 0.9f,
                estimatedPickupMinutes = 16,
            ),
        )

        assertEquals(RecommendationType.REVIEW, decision.type)
    }

    @Test
    fun recommendsAcceptForClearOffer() {
        val decision = policy.evaluate(baseOffer().copy(confidence = 0.9f))

        assertEquals(RecommendationType.ACCEPT, decision.type)
    }

    private fun baseOffer(): TripOffer =
        TripOffer(
            id = "test",
            platform = TripPlatform.UBER,
            pickupAddress = "Av. Corrientes 1234",
            pickupLocation = null,
            destinationAddress = null,
            estimatedFare = null,
            estimatedDistance = null,
            estimatedPickupMinutes = 5,
            passengerRating = null,
            confidence = 0.8f,
            receivedAt = Instant.EPOCH,
            expiresAt = null,
        )
}
