package com.rideunify.domain.action

import com.rideunify.domain.model.TripPlatform
import java.time.Instant

data class TripActionResult(
    val offerId: String,
    val platform: TripPlatform,
    val type: TripActionType,
    val success: Boolean,
    val message: String,
    val completedAt: Instant = Instant.now(),
)
