package com.rideunify.domain.navigation

sealed class ExternalNavigationResult {
    data class Opened(val uri: String, val queryUsed: String?) : ExternalNavigationResult()
    data class Failed(val message: String) : ExternalNavigationResult()
    data object MissingPickup : ExternalNavigationResult()
}
