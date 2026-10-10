package com.thelazybattley.dinkspace.core.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseFactory(private val context: Context) {
    actual fun create(): RoomDatabase.Builder<DinkSpaceDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(DinkSpaceDatabase.NAME)
        return Room.databaseBuilder<DinkSpaceDatabase>(
            context = appContext,
            name = dbFile.absolutePath,
        )
    }
}
