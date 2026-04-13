package com.chame.myapplication.core.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.chame.myapplication.core.database.sync.SyncManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker for syncing products with server
 * Runs periodically in background
 */
@HiltWorker
class SyncProductsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val syncManager: SyncManager
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            syncManager.syncProducts()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()  // Retry later
        }
    }
}
