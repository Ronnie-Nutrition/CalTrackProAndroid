package com.easyaiflows.caltrackpro

import android.app.Application
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CalTrackProApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeCrashlytics()
    }

    private fun initializeCrashlytics() {
        // Crashlytics is automatically initialized by Firebase
        // Configure collection based on build type
        FirebaseCrashlytics.getInstance().apply {
            // Disable crash collection in debug builds
            setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        }
    }
}
