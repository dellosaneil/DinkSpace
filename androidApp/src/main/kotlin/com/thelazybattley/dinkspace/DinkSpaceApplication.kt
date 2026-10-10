package com.thelazybattley.dinkspace

import android.app.Application
import com.thelazybattley.dinkspace.di.ensureFirebaseInitialized
import com.thelazybattley.dinkspace.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class DinkSpaceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ensureFirebaseInitialized()
        initKoin {
            androidLogger()
            androidContext(this@DinkSpaceApplication)
        }
    }
}
