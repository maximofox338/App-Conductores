package com.rideunify.domain.parser

import com.rideunify.domain.accessibility.ScreenSnapshot
import com.rideunify.domain.model.TripPlatform

interface PlatformParser {
    val platform: TripPlatform
    val packageNames: Set<String>

    fun canParse(packageName: String): Boolean = packageNames.contains(packageName)

    fun parse(snapshot: ScreenSnapshot): ParseResult
}
