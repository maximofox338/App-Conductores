package com.rideunify.domain.repository

import com.rideunify.domain.action.TripActionRequest
import com.rideunify.domain.action.TripActionResult
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface TripActionRepository {
    val requests: SharedFlow<TripActionRequest>
    val latestResult: StateFlow<TripActionResult?>

    suspend fun requestAction(request: TripActionRequest)

    suspend fun publishResult(result: TripActionResult)
}
