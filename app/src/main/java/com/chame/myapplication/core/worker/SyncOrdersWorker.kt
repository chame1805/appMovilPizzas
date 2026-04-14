package com.chame.myapplication.core.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.chame.myapplication.core.database.dao.WaiterLocationDao
import com.chame.myapplication.core.network.LocationUpdateDto
import com.chame.myapplication.core.network.WaiterLocationApi
import com.chame.myapplication.core.session.SessionManager
import com.chame.myapplication.features.pizzeriadistrito.domain.repositories.WaiterOrderRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncOrdersWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val waiterOrderRepository: WaiterOrderRepository,
    private val waiterLocationDao: WaiterLocationDao,
    private val waiterLocationApi: WaiterLocationApi,
    private val sessionManager: SessionManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // 1. Sincronizar órdenes (refresca caché Room)
            waiterOrderRepository.getMyOrders()

            // 2. Sincronizar ubicaciones pendientes con el servidor
            syncPendingLocations()

            Result.success()
        } catch (e: Exception) {
            // Reintento automático con backoff exponencial (hasta 3 intentos)
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    private suspend fun syncPendingLocations() {
        val token = sessionManager.token
        if (token.isEmpty()) return

        val pending = waiterLocationDao.getPending()
        if (pending.isEmpty()) return

        val synced = mutableListOf<Long>()
        val failed = mutableListOf<Long>()

        pending.forEach { location ->
            try {
                val response = waiterLocationApi.updateLocation(
                    token = "Bearer $token",
                    body = LocationUpdateDto(
                        waiterId = location.waiterId,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy,
                        timestamp = location.timestamp
                    )
                )
                if (response.isSuccessful) synced.add(location.id)
                else failed.add(location.id)
            } catch (e: Exception) {
                failed.add(location.id)
            }
        }

        if (synced.isNotEmpty()) {
            waiterLocationDao.markSynced(synced)
            waiterLocationDao.clearSynced()
        }
        if (failed.isNotEmpty()) {
            waiterLocationDao.markFailed(failed)
        }
    }
}
