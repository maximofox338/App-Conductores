package com.rideunify.core.data.repository

import com.rideunify.domain.action.TripActionRequest
import com.rideunify.domain.action.TripActionResult
import com.rideunify.domain.repository.TripActionRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class DefaultTripActionRepository @Inject constructor() : TripActionRepository {
    private val mutableRequests = MutableSharedFlow<TripActionRequest>(extraBufferCapacity = 8)
    private val mutableLatestResult = MutableStateFlow<TripActionResult?>(null)

    override val requests: SharedFlow<TripActionRequest> = mutableRequests.asSharedFlow()
    override val latestResult: StateFlow<TripActionResult?> = mutableLatestResult.asStateFlow()

    override suspend fun requestAction(request: TripActionRequest) {
        mutableRequests.emit(request)
    }

    override suspend fun publishResult(result: TripActionResult) {
        mutableLatestResult.value = result
    }
}
