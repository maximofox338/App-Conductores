package com.rideunify.domain.parser

import com.rideunify.domain.model.TripOffer

sealed class ParseResult {
    data class OfferFound(val offer: TripOffer) : ParseResult()
    data class NoOffer(val reason: String) : ParseResult()
}
