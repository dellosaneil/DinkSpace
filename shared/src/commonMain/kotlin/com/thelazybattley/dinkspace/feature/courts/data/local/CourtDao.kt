package com.thelazybattley.dinkspace.feature.courts.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CourtDao {
    @Query("SELECT * FROM courts ORDER BY name COLLATE NOCASE")
    fun observeAll(): Flow<List<CourtEntity>>

    @Query("SELECT * FROM courts WHERE id = :id")
    fun observeById(id: String): Flow<CourtEntity?>

    @Upsert
    suspend fun upsertAll(courts: List<CourtEntity>)

    @Query("DELETE FROM courts")
    suspend fun deleteAll()

    /** Swaps the cache for a fresh snapshot so courts removed on the backend disappear locally. */
    @Transaction
    suspend fun replaceAll(courts: List<CourtEntity>) {
        deleteAll()
        upsertAll(courts)
    }
}
