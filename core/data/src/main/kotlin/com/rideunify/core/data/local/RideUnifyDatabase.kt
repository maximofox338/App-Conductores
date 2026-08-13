package com.rideunify.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TripOfferEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class RideUnifyDatabase : RoomDatabase() {
    abstract fun tripOfferDao(): TripOfferDao
}
