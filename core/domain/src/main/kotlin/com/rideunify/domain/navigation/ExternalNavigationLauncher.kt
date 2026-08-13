package com.rideunify.domain.navigation

import com.rideunify.domain.model.TripOffer

interface ExternalNavigationLauncher {
    fun openPickupInWaze(offer: TripOffer): ExternalNavigationResult
}
