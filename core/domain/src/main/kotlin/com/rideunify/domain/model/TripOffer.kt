package com.rideunify.domain.model

import java.time.Instant

data class TripOffer(
    val id: String,
    val platform: TripPlatform,
    val pickupAddress: String?,
    val pickupLocation: GeoPoint?,
    val destinationAddress: String?,
    val estimatedFare: Money?,
    val estimatedDistance: Distance?,
    val estimatedPickupMinutes: Int?,
    val passengerRating: Float?,
    val confidence: Float,
    val receivedAt: Instant,
    val expiresAt: Instant?,
    val status: TripStatus = TripStatus.PENDING,
    val decision: OfferDecision? = null,
    val extractedFacts: Map<String, String> = emptyMap(),
) {
    fun withDecision(decision: OfferDecision): TripOffer = copy(decision = decision)

    fun withStatus(status: TripStatus): TripOffer = copy(status = status)
}
