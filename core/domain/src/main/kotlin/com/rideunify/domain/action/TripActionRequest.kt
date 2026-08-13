package com.rideunify.domain.action

import com.rideunify.domain.model.TripPlatform
import java.time.Instant

data class TripActionRequest(
    val offerId: String,
    val platform: TripPlatform,
    val type: TripActionType,
    val requestedAt: Instant = Instant.now(),
)
