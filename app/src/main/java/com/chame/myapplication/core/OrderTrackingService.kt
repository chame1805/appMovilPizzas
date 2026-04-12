package com.chame.myapplication.core.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.chame.myapplication.R
import com.chame.myapplication.core.notifications.NotificationChannels
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.chame.myapplication.core.notifications.NotificationManagerHelper

@AndroidEntryPoint
class OrderTrackingService : Service() {
    
    @Inject
    lateinit var notificationManager: NotificationManagerHelper
    
    companion object {
        private const val TAG = "OrderTrackingService"
        const val NOTIFICATION_ID = 1001
        const val ORDER_ID_EXTRA = "order_id"
        const val ORDER_STATUS_EXTRA = "order_status"
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "OrderTrackingService created")
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val orderId = intent?.getStringExtra(ORDER_ID_EXTRA) ?: "unknown"
        val orderStatus = intent?.getStringExtra(ORDER_STATUS_EXTRA) ?: "Procesando tu orden"
        
        Log.d(TAG, "Service started for order: $orderId, status: $orderStatus")
        
        // Crear notificación persistente
        val notification = NotificationCompat.Builder(
            this,
            NotificationChannels.ORDERS_CHANNEL_ID
        )
            .setContentTitle("Orden #$orderId")
            .setContentText(orderStatus)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setProgress(100, 50, true)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(longArrayOf(0, 500))
            .build()
        
        // Iniciar como servicio foreground
        startForeground(NOTIFICATION_ID, notification)
        
        // Simular seguimiento de orden
        trackOrder(orderId)
        
        return START_STICKY
    }
    
    private fun trackOrder(orderId: String) {
        Thread {
            try {
                // Simulación de seguimiento
                val statuses = listOf(
                    "Preparando tu pizza...",
                    "Pizza en el horno...",
                    "Enfriando...",
                    "¡Lista para entregar!"
                )
                
                for ((index, status) in statuses.withIndex()) {
                    Thread.sleep(5000) // 5 segundos entre updates
                    
                    val notification = NotificationCompat.Builder(
                        this,
                        NotificationChannels.ORDERS_CHANNEL_ID
                    )
                        .setContentTitle("Orden #$orderId")
                        .setContentText(status)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setProgress(statuses.size, index + 1, false)
                        .setOngoing(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVibrate(longArrayOf(0, 500))
                        .build()
                    
                    Log.d(TAG, "Updating order $orderId: $status")
                }
                
                // Finalizar servicio
                stopSelf()
            } catch (e: Exception) {
                Log.e(TAG, "Error tracking order", e)
            }
        }.start()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "OrderTrackingService destroyed")
    }
}
