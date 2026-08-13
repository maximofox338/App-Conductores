package com.rideunify.core.data.local

import com.rideunify.domain.model.Distance
import com.rideunify.domain.model.GeoPoint
import com.rideunify.domain.model.Money
import com.rideunify.domain.model.TripOffer
import com.rideunify.domain.model.TripPlatform
import com.rideunify.domain.model.TripStatus
import java.time.Instant

fun TripOffer.toEntity(): TripOfferEntity =
    TripOfferEntity(
        id = id,
        platform = platform.name,
        pickupAddress = pickupAddress,
        pickupLatitude = pickupLocation?.latitude,
        pickupLongitude = pickupLocation?.longitude,
        destinationAddress = destinationAddress,
        estimatedFareAmount = estimatedFare?.amount,
        estimatedFareCurrency = estimatedFare?.currency,
        estimatedDistanceKm = estimatedDistance?.kilometers,
        estimatedPickupMinutes = estimatedPickupMinutes,
        passengerRating = passengerRating,
        confidence = confidence,
        receivedAtEpochMillis = receivedAt.toEpochMilli(),
        expiresAtEpochMillis = expiresAt?.toEpochMilli(),
        status = status.name,
    )

fun TripOfferEntity.toDomain(): TripOffer =
    TripOffer(
        id = id,
        platform = TripPlatform.valueOf(platform),
        pickupAddress = pickupAddress,
        pickupLocation = if (pickupLatitude != null && pickupLongitude != null) {
            GeoPoint(pickupLatitude, pickupLongitude)
        } else {
            null
        },
        destinationAddress = destinationAddress,
        estimatedFare = estimatedFareAmount?.let { Money(it, estimatedFareCurrency ?: "ARS") },
        estimatedDistance = estimatedDistanceKm?.let { Distance(it) },
        estimatedPickupMinutes = estimatedPickupMinutes,
        passengerRating = passengerRating,
        confidence = confidence,
        receivedAt = Instant.ofEpochMilli(receivedAtEpochMillis),
        expiresAt = expiresAtEpochMillis?.let(Instant::ofEpochMilli),
        status = TripStatus.valueOf(status),
    )
