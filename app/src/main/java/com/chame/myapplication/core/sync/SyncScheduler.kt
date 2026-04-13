package com.chame.myapplication.core.sync

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * SyncScheduler manages periodic background sync tasks
 * Schedules WorkManager tasks for syncing orders and products
 */
@Singleton
class SyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Schedule periodic sync of orders
     * Runs every 15 minutes
     */
    fun scheduleOrderSync() {
        try {
            val syncOrdersRequest = PeriodicWorkRequestBuilder<SyncOrdersWorker>(
                15, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "sync_orders",
                ExistingPeriodicWorkPolicy.KEEP,
                syncOrdersRequest
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Schedule periodic sync of products
     * Runs every 30 minutes
     */
    fun scheduleProductSync() {
        try {
            val syncProductsRequest = PeriodicWorkRequestBuilder<SyncProductsWorker>(
                30, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "sync_products",
                ExistingPeriodicWorkPolicy.KEEP,
                syncProductsRequest
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Schedule all sync tasks
     */
    fun scheduleAllSync() {
        scheduleOrderSync()
        scheduleProductSync()
    }

    /**
     * Cancel all sync tasks
     */
    fun cancelAllSync() {
        try {
            WorkManager.getInstance(context).cancelUniqueWork("sync_orders")
            WorkManager.getInstance(context).cancelUniqueWork("sync_products")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
