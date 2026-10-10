package com.thelazybattley.dinkspace.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.thelazybattley.dinkspace.core.data.SystemDateProvider
import com.thelazybattley.dinkspace.core.data.database.DatabaseFactory
import com.thelazybattley.dinkspace.core.data.database.DinkSpaceDatabase
import com.thelazybattley.dinkspace.core.data.database.MIGRATION_1_2
import com.thelazybattley.dinkspace.core.domain.DateProvider
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val coreModule = module {
    single<DinkSpaceDatabase> {
        get<DatabaseFactory>()
            .create()
            .addMigrations(MIGRATION_1_2)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single<DateProvider> { SystemDateProvider() }
    single { Firebase.firestore }
    single { Firebase.auth }
}
