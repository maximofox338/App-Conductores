package com.rideunify.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip_offers")
data class TripOfferEntity(
    @PrimaryKey val id: String,
    val platform: String,
    val pickupAddress: String?,
    val pickupLatitude: Double?,
    val pickupLongitude: Double?,
    val destinationAddress: String?,
    val estimatedFareAmount: Double?,
    val estimatedFareCurrency: String?,
    val estimatedDistanceKm: Double?,
    val estimatedPickupMinutes: Int?,
    val passengerRating: Float?,
    val confidence: Float,
    val receivedAtEpochMillis: Long,
    val expiresAtEpochMillis: Long?,
    val status: String,
)
