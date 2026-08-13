package com.rideunify.platform.uber

import com.rideunify.core.accessibility.AccessibilityActionExecutor
import com.rideunify.domain.parser.PlatformParser
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class UberPlatformModule {
    @Binds
    @IntoSet
    abstract fun bindParser(parser: UberParser): PlatformParser

    @Binds
    @IntoSet
    abstract fun bindActionExecutor(
        executor: UberAccessibilityActionExecutor,
    ): AccessibilityActionExecutor
}
