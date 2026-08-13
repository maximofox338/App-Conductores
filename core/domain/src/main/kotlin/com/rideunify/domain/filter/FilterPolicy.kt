package com.rideunify.domain.filter

import com.rideunify.domain.model.OfferDecision
import com.rideunify.domain.model.TripOffer

interface FilterPolicy {
    fun evaluate(offer: TripOffer): OfferDecision
}
