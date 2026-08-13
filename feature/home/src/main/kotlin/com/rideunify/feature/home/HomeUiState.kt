package com.rideunify.feature.home

import com.rideunify.domain.action.TripActionResult
import com.rideunify.domain.model.TripOffer

data class HomeUiState(
    val offer: TripOffer? = null,
    val latestActionResult: TripActionResult? = null,
    val isActionPending: Boolean = false,
    val navigationMessage: String? = null,
)
