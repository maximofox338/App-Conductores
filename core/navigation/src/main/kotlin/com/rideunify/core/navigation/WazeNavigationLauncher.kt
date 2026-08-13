package com.rideunify.core.navigation

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.rideunify.domain.model.TripOffer
import com.rideunify.domain.navigation.ExternalNavigationLauncher
import com.rideunify.domain.navigation.ExternalNavigationResult
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WazeNavigationLauncher @Inject constructor(
    @ApplicationContext private val context: Context,
    private val deepLinkBuilder: WazeDeepLinkBuilder,
) : ExternalNavigationLauncher {
    override fun openPickupInWaze(offer: TripOffer): ExternalNavigationResult {
        val deepLink = deepLinkBuilder.build(offer, context.packageName)
            ?: return ExternalNavigationResult.MissingPickup
        val uri = Uri.parse(deepLink.uri)

        val intent = Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(intent)
            ExternalNavigationResult.Opened(
                uri = deepLink.uri,
                queryUsed = deepLink.queryUsed,
            )
        } catch (activityNotFound: ActivityNotFoundException) {
            openWazeStoreFallback(deepLink)
        }
    }

    private fun openWazeStoreFallback(deepLink: WazeDeepLink): ExternalNavigationResult =
        try {
            val storeIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=com.waze"),
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(storeIntent)
            ExternalNavigationResult.Opened(
                uri = deepLink.uri,
                queryUsed = deepLink.queryUsed,
            )
        } catch (activityNotFound: ActivityNotFoundException) {
            ExternalNavigationResult.Failed("No se pudo abrir Waze ni la tienda.")
        }
}
