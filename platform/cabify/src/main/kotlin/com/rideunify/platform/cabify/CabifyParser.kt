package com.rideunify.platform.cabify

import com.rideunify.domain.accessibility.ScreenSnapshot
import com.rideunify.domain.model.Distance
import com.rideunify.domain.model.Money
import com.rideunify.domain.model.TripOffer
import com.rideunify.domain.model.TripPlatform
import com.rideunify.domain.parser.ParseResult
import com.rideunify.domain.parser.PlatformParser
import java.time.Duration
import javax.inject.Inject

class CabifyParser @Inject constructor() : PlatformParser {
    override val platform: TripPlatform = TripPlatform.CABIFY
    override val packageNames: Set<String> = setOf("com.cabify.driver", "com.cabify.drivers")

    override fun parse(snapshot: ScreenSnapshot): ParseResult {
        val labels = snapshot.flattenedLabels()
        if (!labels.looksLikeOffer()) {
            return ParseResult.NoOffer("No Cabify offer markers found.")
        }

        val pickupAddress = labels.firstAddressCandidate()
        val estimatedFare = labels.firstMoney()
        val estimatedDistance = labels.firstDistance()
        val pickupMinutes = labels.firstMinutes()
        val confidence = buildConfidence(
            hasAddress = pickupAddress != null,
            hasFare = estimatedFare != null,
            hasPickupTime = pickupMinutes != null,
        )

        return ParseResult.OfferFound(
            TripOffer(
                id = "cabify-${labels.joinToString("|").hashCode()}",
                platform = TripPlatform.CABIFY,
                pickupAddress = pickupAddress,
                pickupLocation = null,
                destinationAddress = labels.destinationCandidate(),
                estimatedFare = estimatedFare,
                estimatedDistance = estimatedDistance,
                estimatedPickupMinutes = pickupMinutes,
                passengerRating = null,
                confidence = confidence,
                receivedAt = snapshot.capturedAt,
                expiresAt = snapshot.capturedAt.plus(Duration.ofSeconds(20)),
                extractedFacts = labels.take(12).associateBy { it },
            ),
        )
    }

    private fun List<String>.looksLikeOffer(): Boolean {
        val text = joinToString(" ").lowercase()
        val hasAction = text.contains("aceptar") || text.contains("accept")
        val hasTripMarker = text.contains("viaje") || text.contains("cabify") || text.contains("solicitud")
        return hasAction && hasTripMarker
    }
}

private fun buildConfidence(
    hasAddress: Boolean,
    hasFare: Boolean,
    hasPickupTime: Boolean,
): Float {
    var confidence = 0.45f
    if (hasAddress) confidence += 0.2f
    if (hasFare) confidence += 0.2f
    if (hasPickupTime) confidence += 0.15f
    return confidence.coerceAtMost(0.95f)
}

private fun List<String>.firstAddressCandidate(): String? =
    firstOrNull { label ->
        val normalized = label.lowercase()
        normalized.any(Char::isDigit) &&
            !normalized.contains("$") &&
            !normalized.contains("ars") &&
            !normalized.contains("min") &&
            !normalized.contains("km")
    }

private fun List<String>.destinationCandidate(): String? =
    windowed(size = 2, step = 1, partialWindows = true)
        .firstOrNull { window ->
            window.firstOrNull()?.lowercase()?.contains("destino") == true ||
                window.firstOrNull()?.lowercase()?.contains("destination") == true
        }
        ?.lastOrNull()

private fun List<String>.firstMoney(): Money? {
    val regex = Regex("""(?:ARS|\$)\s*([0-9]+(?:[.,][0-9]{1,2})?)""", RegexOption.IGNORE_CASE)
    val match = firstNotNullOfOrNull { label -> regex.find(label) } ?: return null
    val amount = match.groupValues[1].replace(".", "").replace(",", ".").toDoubleOrNull() ?: return null
    return Money(amount = amount)
}

private fun List<String>.firstDistance(): Distance? {
    val regex = Regex("""([0-9]+(?:[.,][0-9]+)?)\s*km""", RegexOption.IGNORE_CASE)
    val match = firstNotNullOfOrNull { label -> regex.find(label) } ?: return null
    val km = match.groupValues[1].replace(",", ".").toDoubleOrNull() ?: return null
    return Distance(kilometers = km)
}

private fun List<String>.firstMinutes(): Int? {
    val regex = Regex("""([0-9]{1,2})\s*(min|mins|minutos)""", RegexOption.IGNORE_CASE)
    val match = firstNotNullOfOrNull { label -> regex.find(label) } ?: return null
    return match.groupValues[1].toIntOrNull()
}
