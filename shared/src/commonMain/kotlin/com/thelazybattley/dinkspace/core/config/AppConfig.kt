package com.thelazybattley.dinkspace.core.config

object AppConfig {
    /**
     * When true, features read from in-memory dummy data instead of Firebase,
     * so the app runs without google-services.json / GoogleService-Info.plist.
     * Set to false once Firebase is configured.
     */
    const val USE_FAKE_DATA = true
}
