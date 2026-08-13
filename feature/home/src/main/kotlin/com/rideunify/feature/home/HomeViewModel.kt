package com.rideunify.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rideunify.domain.action.TripActionRequest
import com.rideunify.domain.action.TripActionType
import com.rideunify.domain.filter.FilterPolicy
import com.rideunify.domain.model.TripStatus
import com.rideunify.domain.navigation.ExternalNavigationLauncher
import com.rideunify.domain.navigation.ExternalNavigationResult
import com.rideunify.domain.repository.TripActionRepository
import com.rideunify.domain.repository.TripOfferRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tripOfferRepository: TripOfferRepository,
    private val tripActionRepository: TripActionRepository,
    private val filterPolicy: FilterPolicy,
    private val externalNavigationLauncher: ExternalNavigationLauncher,
) : ViewModel() {
    private val actionPending = MutableStateFlow(false)
    private val navigationMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<HomeUiState> =
        combine(
            tripOfferRepository.currentOffer,
            tripActionRepository.latestResult,
            actionPending,
            navigationMessage,
        ) { offer, latestResult, pending, message ->
            HomeUiState(
                offer = offer?.let { current ->
                    if (current.decision == null) current.withDecision(filterPolicy.evaluate(current)) else current
                },
                latestActionResult = latestResult,
                isActionPending = pending,
                navigationMessage = message,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )

    fun onAcceptClicked() {
        requestAction(TripActionType.ACCEPT)
    }

    fun onDeclineClicked() {
        requestAction(TripActionType.DECLINE)
    }

    fun onOpenWazeClicked() {
        val offer = uiState.value.offer ?: return
        val result = externalNavigationLauncher.openPickupInWaze(offer)
        navigationMessage.value = when (result) {
            is ExternalNavigationResult.Opened -> "Waze abierto con el punto de busqueda."
            is ExternalNavigationResult.Failed -> result.message
            ExternalNavigationResult.MissingPickup -> "No hay punto de busqueda para enviar a Waze."
        }
    }

    fun onClearOfferClicked() {
        viewModelScope.launch {
            tripOfferRepository.clearCurrentOffer()
            navigationMessage.value = null
        }
    }

    private fun requestAction(type: TripActionType) {
        val offer = uiState.value.offer ?: return
        if (offer.status != TripStatus.PENDING) return

        viewModelScope.launch {
            actionPending.value = true
            navigationMessage.value = null
            tripActionRepository.requestAction(
                TripActionRequest(
                    offerId = offer.id,
                    platform = offer.platform,
                    type = type,
                ),
            )
            actionPending.value = false
        }
    }
}
