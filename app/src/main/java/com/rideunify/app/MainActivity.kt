package com.rideunify.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.rideunify.app.ui.theme.RideUnifyTheme
import com.rideunify.feature.home.HomeRoute
import com.rideunify.feature.settings.SettingsScreen
import com.rideunify.feature.trips.TripsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RideUnifyTheme {
                RideUnifyApp()
            }
        }
    }
}

@Composable
private fun RideUnifyApp() {
    var selectedTab by remember { mutableStateOf(AppTab.HOME) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(text = stringResource(tab.labelRes)) },
                        icon = {},
                    )
                }
            }
        },
    ) { innerPadding ->
        when (selectedTab) {
            AppTab.HOME -> HomeRoute(modifier = Modifier.padding(innerPadding))
            AppTab.TRIPS -> TripsScreen(modifier = Modifier.padding(innerPadding))
            AppTab.SETTINGS -> SettingsScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}

private enum class AppTab(
    val labelRes: Int,
) {
    HOME(R.string.tab_home),
    TRIPS(R.string.tab_trips),
    SETTINGS(R.string.tab_settings),
}
