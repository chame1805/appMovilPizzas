package com.chame.myapplication.core.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.chame.myapplication.R
import javax.inject.Inject

class NotificationManagerHelper @Inject constructor(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(
        NotificationManager::class.java
    )
    
    fun showOrderNotification(
        orderId: String,
        title: String,
        message: String,
        notificationId: Int = orderId.hashCode()
    ) {
        val notification = NotificationCompat.Builder(context, NotificationChannels.ORDERS_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()
        
        notificationManager.notify(notificationId, notification)
    }
    
    fun showPromotionNotification(
        promotionId: String,
        title: String,
        message: String,
        notificationId: Int = promotionId.hashCode()
    ) {
        val notification = NotificationCompat.Builder(context, NotificationChannels.PROMOTIONS_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(notificationId, notification)
    }
    
    fun showUrgentNotification(
        alertId: String,
        title: String,
        message: String,
        notificationId: Int = alertId.hashCode()
    ) {
        val notification = NotificationCompat.Builder(context, NotificationChannels.URGENT_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 250, 500, 250, 500))
            .setLights(0xFF0000, 1000, 1000)
            .build()
        
        notificationManager.notify(notificationId, notification)
    }
    
    fun cancelNotification(notificationId: Int) {
        notificationManager.cancel(notificationId)
    }
    
    fun cancelAllNotifications() {
        notificationManager.cancelAll()
    }
}
