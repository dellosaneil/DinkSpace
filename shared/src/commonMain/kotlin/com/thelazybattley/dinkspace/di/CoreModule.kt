package com.thelazybattley.dinkspace.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.thelazybattley.dinkspace.core.data.SystemDateProvider
import com.thelazybattley.dinkspace.core.data.database.DatabaseFactory
import com.thelazybattley.dinkspace.core.data.database.DinkSpaceDatabase
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
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single<DateProvider> { SystemDateProvider() }
    single { Firebase.firestore }
    single { Firebase.auth }
}
