package com.thelazybattley.dinkspace.core.data.database

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

/**
 * v2 drops courts.email and hourlyRate and adds peak/off-peak rates. The courts table only
 * caches Firestore, so it is rebuilt empty and refilled on the next refresh.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `courts`")
        connection.execSQL(
            "CREATE TABLE IF NOT EXISTS `courts` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, " +
                "`area` TEXT NOT NULL, `address` TEXT NOT NULL, `courtCount` INTEGER NOT NULL, " +
                "`setting` TEXT NOT NULL, `openingHour` INTEGER NOT NULL, `closingHour` INTEGER NOT NULL, " +
                "`phoneNumber` TEXT NOT NULL, `messengerUrl` TEXT, `logoUrl` TEXT, `photoUrls` TEXT NOT NULL, " +
                "`standardRate` REAL, `peakRate` REAL, `peakStartHour` INTEGER, `peakEndHour` INTEGER, " +
                "PRIMARY KEY(`id`))",
        )
    }
}
