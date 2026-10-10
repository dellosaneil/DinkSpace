package com.thelazybattley.dinkspace.core.data.database

import androidx.room.RoomDatabase

/** Supplies a platform-specific [RoomDatabase.Builder]; driver and dispatcher are set in common code. */
expect class DatabaseFactory {
    fun create(): RoomDatabase.Builder<DinkSpaceDatabase>
}
