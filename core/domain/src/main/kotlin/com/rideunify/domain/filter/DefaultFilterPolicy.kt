package com.rideunify.domain.filter

import com.rideunify.domain.model.OfferDecision
import com.rideunify.domain.model.RecommendationType
import com.rideunify.domain.model.TripOffer

class DefaultFilterPolicy(
    private val minimumConfidence: Float = 0.55f,
) : FilterPolicy {
    override fun evaluate(offer: TripOffer): OfferDecision =
        when {
            offer.confidence < minimumConfidence -> OfferDecision(
                type = RecommendationType.REVIEW,
                reason = "Datos incompletos: revisar antes de aceptar.",
            )
            offer.estimatedPickupMinutes != null && offer.estimatedPickupMinutes > 12 -> OfferDecision(
                type = RecommendationType.REVIEW,
                reason = "El punto de búsqueda parece lejos.",
            )
            else -> OfferDecision(
                type = RecommendationType.ACCEPT,
                reason = "La solicitud parece clara para decidir.",
            )
        }
}
