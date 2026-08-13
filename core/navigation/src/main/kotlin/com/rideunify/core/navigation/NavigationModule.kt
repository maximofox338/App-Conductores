package com.rideunify.core.navigation

import com.rideunify.domain.navigation.ExternalNavigationLauncher
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigationModule {
    @Binds
    @Singleton
    abstract fun bindExternalNavigationLauncher(
        launcher: WazeNavigationLauncher,
    ): ExternalNavigationLauncher
}
