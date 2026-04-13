package com.chame.myapplication

import android.app.Application
import com.chame.myapplication.core.sync.SyncScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class PizzaApplication : Application() {

    @Inject
    lateinit var syncScheduler: SyncScheduler

    override fun onCreate() {
        super.onCreate()
        // Schedule periodic sync tasks
        try {
            syncScheduler.scheduleAllSync()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
