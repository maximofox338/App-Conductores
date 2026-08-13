package com.rideunify.core.accessibility

import android.view.accessibility.AccessibilityNodeInfo
import com.rideunify.domain.action.TripActionRequest
import com.rideunify.domain.action.TripActionResult
import com.rideunify.domain.model.TripPlatform

interface AccessibilityActionExecutor {
    val platform: TripPlatform

    fun execute(
        root: AccessibilityNodeInfo?,
        request: TripActionRequest,
    ): TripActionResult
}
