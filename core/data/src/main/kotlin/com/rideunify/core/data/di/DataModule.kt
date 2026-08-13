package com.rideunify.core.data.di

import android.content.Context
import androidx.room.Room
import com.rideunify.core.data.local.RideUnifyDatabase
import com.rideunify.core.data.local.TripOfferDao
import com.rideunify.core.data.repository.DefaultTripActionRepository
import com.rideunify.core.data.repository.DefaultTripOfferRepository
import com.rideunify.domain.filter.DefaultFilterPolicy
import com.rideunify.domain.filter.FilterPolicy
import com.rideunify.domain.repository.TripActionRepository
import com.rideunify.domain.repository.TripOfferRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTripOfferRepository(
        repository: DefaultTripOfferRepository,
    ): TripOfferRepository

    @Binds
    @Singleton
    abstract fun bindTripActionRepository(
        repository: DefaultTripActionRepository,
    ): TripActionRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): RideUnifyDatabase =
        Room.databaseBuilder(
            context,
            RideUnifyDatabase::class.java,
            "rideunify.db",
        ).build()

    @Provides
    fun provideTripOfferDao(database: RideUnifyDatabase): TripOfferDao =
        database.tripOfferDao()

    @Provides
    @Singleton
    fun provideFilterPolicy(): FilterPolicy = DefaultFilterPolicy()
}
