package com.rideunify.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rideunify.domain.action.TripActionResult
import com.rideunify.domain.action.TripActionType
import com.rideunify.domain.model.Distance
import com.rideunify.domain.model.Money
import com.rideunify.domain.model.OfferDecision
import com.rideunify.domain.model.RecommendationType
import com.rideunify.domain.model.TripOffer
import com.rideunify.domain.model.TripPlatform
import com.rideunify.domain.model.TripStatus
import java.time.Instant

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    HomeScreen(
        state = state,
        onAccept = viewModel::onAcceptClicked,
        onDecline = viewModel::onDeclineClicked,
        onOpenWaze = viewModel::onOpenWazeClicked,
        onClearOffer = viewModel::onClearOfferClicked,
        modifier = modifier,
    )
}

@Composable
fun HomeScreen(
    state: HomeUiState,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onOpenWaze: () -> Unit,
    onClearOffer: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(R.string.home_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        ServiceStatusCard()

        val offer = state.offer
        if (offer == null) {
            EmptyOfferCard()
        } else {
            OfferCard(
                offer = offer,
                latestResult = state.latestActionResult,
                isActionPending = state.isActionPending,
                onAccept = onAccept,
                onDecline = onDecline,
                onOpenWaze = onOpenWaze,
                onClearOffer = onClearOffer,
            )
        }

        state.navigationMessage?.let { message ->
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ServiceStatusCard() {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.service_status_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.service_status_body),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun EmptyOfferCard() {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(R.string.no_offer_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = stringResource(R.string.no_offer_body),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun OfferCard(
    offer: TripOffer,
    latestResult: TripActionResult?,
    isActionPending: Boolean,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onOpenWaze: () -> Unit,
    onClearOffer: () -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "${stringResource(R.string.current_offer_title)} · ${offer.platform.displayName}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            OfferFact(label = stringResource(R.string.pickup_label), value = offer.pickupAddress)
            OfferFact(label = stringResource(R.string.destination_label), value = offer.destinationAddress)
            OfferFact(label = stringResource(R.string.fare_label), value = offer.estimatedFare.format())
            OfferFact(label = stringResource(R.string.distance_label), value = offer.estimatedDistance.format())
            OfferFact(
                label = stringResource(R.string.pickup_time_label),
                value = offer.estimatedPickupMinutes?.let { "$it min" },
            )
            OfferFact(
                label = stringResource(R.string.confidence_label),
                value = "${(offer.confidence * 100).toInt()}%",
            )
            OfferFact(
                label = stringResource(R.string.decision_label),
                value = offer.decision?.let { "${it.type.displayText()}: ${it.reason}" },
            )

            if (isActionPending) {
                Text(
                    text = stringResource(R.string.action_pending),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            latestResult?.takeIf { it.offerId == offer.id }?.let { result ->
                Text(
                    text = "${stringResource(R.string.latest_result_label)}: ${result.message}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onAccept,
                    enabled = offer.status == TripStatus.PENDING && !isActionPending,
                ) {
                    Text(text = stringResource(R.string.accept_action))
                }
                OutlinedButton(
                    onClick = onDecline,
                    enabled = offer.status == TripStatus.PENDING && !isActionPending,
                ) {
                    Text(text = stringResource(R.string.decline_action))
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onOpenWaze,
                    enabled = offer.status == TripStatus.ACCEPTED,
                ) {
                    Text(text = stringResource(R.string.open_waze_action))
                }
                OutlinedButton(onClick = onClearOffer) {
                    Text(text = stringResource(R.string.clear_offer_action))
                }
            }
        }
    }
}

@Composable
private fun OfferFact(
    label: String,
    value: String?,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
        Text(
            text = value ?: stringResource(R.string.unknown_value),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

private fun Money?.format(): String? =
    this?.let { "${it.currency} ${it.amount.toInt()}" }

private fun Distance?.format(): String? =
    this?.let { "${it.kilometers} km" }

private fun RecommendationType.displayText(): String =
    when (this) {
        RecommendationType.ACCEPT -> "Aceptar"
        RecommendationType.REVIEW -> "Revisar"
        RecommendationType.DECLINE -> "Rechazar"
    }

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen(
        state = HomeUiState(
            offer = TripOffer(
                id = "preview",
                platform = TripPlatform.UBER,
                pickupAddress = "Av. Corrientes 1234",
                pickupLocation = null,
                destinationAddress = "Palermo",
                estimatedFare = Money(4200.0),
                estimatedDistance = Distance(4.2),
                estimatedPickupMinutes = 5,
                passengerRating = 4.9f,
                confidence = 0.82f,
                receivedAt = Instant.now(),
                expiresAt = null,
                decision = OfferDecision(
                    type = RecommendationType.ACCEPT,
                    reason = "La solicitud parece clara para decidir.",
                ),
            ),
        ),
        onAccept = {},
        onDecline = {},
        onOpenWaze = {},
        onClearOffer = {},
    )
}
