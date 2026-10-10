package com.thelazybattley.dinkspace.di

import com.thelazybattley.dinkspace.core.data.database.DatabaseFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single { DatabaseFactory() }
}
