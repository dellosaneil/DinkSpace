package com.thelazybattley.dinkspace.core.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.thelazybattley.dinkspace.feature.courts.data.local.CourtDao
import com.thelazybattley.dinkspace.feature.courts.data.local.CourtEntity

@Database(
    entities = [CourtEntity::class],
    version = 1,
)
@TypeConverters(StringListConverter::class)
@ConstructedBy(DinkSpaceDatabaseConstructor::class)
abstract class DinkSpaceDatabase : RoomDatabase() {
    abstract fun courtDao(): CourtDao

    companion object {
        const val NAME = "dinkspace.db"
    }
}

// Room generates the actual implementations for each platform.
@Suppress("KotlinNoActualForExpect")
expect object DinkSpaceDatabaseConstructor : RoomDatabaseConstructor<DinkSpaceDatabase> {
    override fun initialize(): DinkSpaceDatabase
}
