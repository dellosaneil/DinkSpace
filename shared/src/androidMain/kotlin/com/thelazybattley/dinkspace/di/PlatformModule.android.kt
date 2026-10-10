package com.thelazybattley.dinkspace.di

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.thelazybattley.dinkspace.core.data.database.DatabaseFactory
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

fun Context.ensureFirebaseInitialized() {
    if (FirebaseApp.getApps(this).isEmpty()) {
        val initialized = try {
            FirebaseApp.initializeApp(this) != null
        } catch (_: Exception) {
            false
        }
        if (!initialized) {
            val options = FirebaseOptions.Builder()
                .setApplicationId("1:123456789012:android:1234567890123456789012")
                .setApiKey("AIzaSyDummyApiKeyForDinkSpaceApp12345")
                .setProjectId("dinkspace-app")
                .build()
            FirebaseApp.initializeApp(this, options)
        }
    }
}

actual val platformModule: Module = module {
    single {
        val context = androidApplication()
        context.ensureFirebaseInitialized()
        DatabaseFactory(context)
    }
}
