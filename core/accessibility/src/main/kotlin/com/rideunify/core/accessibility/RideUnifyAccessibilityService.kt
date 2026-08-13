package com.rideunify.core.accessibility

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.rideunify.domain.action.TripActionResult
import com.rideunify.domain.parser.ParseResult
import com.rideunify.domain.parser.PlatformParser
import com.rideunify.domain.repository.TripActionRepository
import com.rideunify.domain.repository.TripOfferRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RideUnifyAccessibilityService : AccessibilityService() {
    @Inject lateinit var parsers: Set<@JvmSuppressWildcards PlatformParser>
    @Inject lateinit var executors: Set<@JvmSuppressWildcards AccessibilityActionExecutor>
    @Inject lateinit var snapshotMapper: AccessibilitySnapshotMapper
    @Inject lateinit var tripOfferRepository: TripOfferRepository
    @Inject lateinit var tripActionRepository: TripActionRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onServiceConnected() {
        super.onServiceConnected()
        serviceScope.launch {
            tripActionRepository.requests.collectLatest { request ->
                val result = executors
                    .firstOrNull { it.platform == request.platform }
                    ?.execute(rootInActiveWindow, request)
                    ?: TripActionResult(
                        offerId = request.offerId,
                        platform = request.platform,
                        type = request.type,
                        success = false,
                        message = "No hay ejecutor disponible para ${request.platform.displayName}.",
                    )

                tripActionRepository.publishResult(result)
                tripOfferRepository.applyActionResult(result)
            }
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val packageName = event?.packageName?.toString() ?: return
        val parser = parsers.firstOrNull { it.canParse(packageName) } ?: return
        val root = rootInActiveWindow ?: return
        val snapshot = snapshotMapper.map(packageName, root)

        serviceScope.launch {
            when (val result = parser.parse(snapshot)) {
                is ParseResult.OfferFound -> tripOfferRepository.publishOffer(result.offer)
                is ParseResult.NoOffer -> Unit
            }
        }
    }

    override fun onInterrupt() = Unit

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
