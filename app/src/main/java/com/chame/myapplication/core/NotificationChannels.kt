package com.chame.myapplication.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationChannels {
    
    const val ORDERS_CHANNEL_ID = "orders_channel"
    const val PROMOTIONS_CHANNEL_ID = "promotions_channel"
    const val URGENT_CHANNEL_ID = "urgent_channel"
    
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(
                NotificationManager::class.java
            )
            
            // Canal para órdenes
            val ordersChannel = NotificationChannel(
                ORDERS_CHANNEL_ID,
                "Notificaciones de Órdenes",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Recibe actualizaciones sobre tus órdenes"
                enableVibration(true)
                setShowBadge(true)
            }
            
            // Canal para promociones
            val promotionsChannel = NotificationChannel(
                PROMOTIONS_CHANNEL_ID,
                "Promociones y Ofertas",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Ofertas especiales y promociones"
                enableVibration(false)
                setShowBadge(true)
            }
            
            // Canal para notificaciones urgentes
            val urgentChannel = NotificationChannel(
                URGENT_CHANNEL_ID,
                "Notificaciones Urgentes",
                NotificationManager.IMPORTANCE_MAX
            ).apply {
                description = "Alertas urgentes que requieren atención inmediata"
                enableVibration(true)
                enableLights(true)
                setShowBadge(true)
            }
            
            notificationManager.createNotificationChannel(ordersChannel)
            notificationManager.createNotificationChannel(promotionsChannel)
            notificationManager.createNotificationChannel(urgentChannel)
        }
    }
}
