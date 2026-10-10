package com.thelazybattley.dinkspace.di

import org.koin.core.module.Module

/** Bindings that need platform types, such as the Android Context. */
expect val platformModule: Module
