package com.rideunify.core.data.repository

import com.rideunify.core.data.local.TripOfferDao
import com.rideunify.core.data.local.toEntity
import com.rideunify.domain.action.TripActionResult
import com.rideunify.domain.action.TripActionType
import com.rideunify.domain.model.TripOffer
import com.rideunify.domain.model.TripStatus
import com.rideunify.domain.repository.TripOfferRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class DefaultTripOfferRepository @Inject constructor(
    private val tripOfferDao: TripOfferDao,
) : TripOfferRepository {
    private val mutableCurrentOffer = MutableStateFlow<TripOffer?>(null)

    override val currentOffer: StateFlow<TripOffer?> = mutableCurrentOffer.asStateFlow()

    override suspend fun publishOffer(offer: TripOffer) {
        val current = mutableCurrentOffer.value
        if (current?.id == offer.id && current.status != TripStatus.PENDING) return
        mutableCurrentOffer.value = offer
    }

    override suspend fun applyActionResult(result: TripActionResult) {
        val current = mutableCurrentOffer.value ?: return
        if (current.id != result.offerId) return

        val status = when {
            !result.success -> TripStatus.FAILED
            result.type == TripActionType.ACCEPT -> TripStatus.ACCEPTED
            else -> TripStatus.DECLINED
        }
        val updated = current.withStatus(status)
        mutableCurrentOffer.value = updated
        tripOfferDao.upsert(updated.toEntity())
    }

    override suspend fun clearCurrentOffer() {
        mutableCurrentOffer.value = null
    }
}
