package com.rideunify.domain.repository

import com.rideunify.domain.action.TripActionResult
import com.rideunify.domain.model.TripOffer
import kotlinx.coroutines.flow.StateFlow

interface TripOfferRepository {
    val currentOffer: StateFlow<TripOffer?>

    suspend fun publishOffer(offer: TripOffer)

    suspend fun applyActionResult(result: TripActionResult)

    suspend fun clearCurrentOffer()
}
