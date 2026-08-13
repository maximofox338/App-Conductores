package com.rideunify.platform.uber

import android.view.accessibility.AccessibilityNodeInfo
import com.rideunify.core.accessibility.AccessibilityActionExecutor
import com.rideunify.core.accessibility.TextNodeClicker
import com.rideunify.domain.action.TripActionRequest
import com.rideunify.domain.action.TripActionResult
import com.rideunify.domain.action.TripActionType
import com.rideunify.domain.model.TripPlatform
import javax.inject.Inject

class UberAccessibilityActionExecutor @Inject constructor(
    private val textNodeClicker: TextNodeClicker,
) : AccessibilityActionExecutor {
    override val platform: TripPlatform = TripPlatform.UBER

    override fun execute(
        root: AccessibilityNodeInfo?,
        request: TripActionRequest,
    ): TripActionResult {
        val labels = when (request.type) {
            TripActionType.ACCEPT -> listOf("Aceptar", "Accept")
            TripActionType.DECLINE -> listOf("Rechazar", "No aceptar", "Decline")
        }
        val clicked = textNodeClicker.clickFirstMatching(root, labels)
        return TripActionResult(
            offerId = request.offerId,
            platform = request.platform,
            type = request.type,
            success = clicked,
            message = if (clicked) {
                "Accion enviada a Uber."
            } else {
                "No se encontro el boton esperado en Uber."
            },
        )
    }
}
