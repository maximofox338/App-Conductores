package com.rideunify.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TripOfferDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TripOfferEntity)

    @Query("SELECT * FROM trip_offers ORDER BY receivedAtEpochMillis DESC")
    fun observeHistory(): Flow<List<TripOfferEntity>>
}
