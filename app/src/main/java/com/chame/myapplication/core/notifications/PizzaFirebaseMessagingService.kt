package com.chame.myapplication.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.chame.myapplication.MainActivity
import com.chame.myapplication.R
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PizzaFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        // Canal principal de órdenes — prioridad ALTA con sonido y vibración
        const val CHANNEL_ORDERS = "channel_orders"
        const val CHANNEL_ORDERS_NAME = "Órdenes Pizzería"

        // Canal de sincronización — prioridad NORMAL (silencioso)
        const val CHANNEL_SYNC = "channel_sync"
        const val CHANNEL_SYNC_NAME = "Sincronización"

        // Canal de alertas — prioridad ALTA con luz y sonido
        const val CHANNEL_ALERTS = "channel_alerts"
        const val CHANNEL_ALERTS_NAME = "Alertas Operacionales"

        // Compatibilidad con código anterior
        const val CHANNEL_ID = CHANNEL_ORDERS
        const val CHANNEL_NAME = CHANNEL_ORDERS_NAME
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: "Pizzería Distrito"
        val body = message.notification?.body ?: message.data["body"] ?: "Tienes una notificación"
        val type = message.data["type"] ?: "order"

        val channelId = when (type) {
            "sync" -> CHANNEL_SYNC
            "alert" -> CHANNEL_ALERTS
            else -> CHANNEL_ORDERS
        }

        showNotification(title, body, channelId)
    }

    override fun onNewToken(token: String) {
        getSharedPreferences("fcm_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("fcm_token", token)
            .apply()
        // Re-suscribir al topic cuando el token se renueva
        subscribeToTopics()
    }

    fun subscribeToTopics() {
        FirebaseMessaging.getInstance().subscribeToTopic("ordenes")
        FirebaseMessaging.getInstance().subscribeToTopic("alertas")
        FirebaseMessaging.getInstance().subscribeToTopic("cocina")
    }

    private fun showNotification(title: String, body: String, channelId: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createAllChannels(manager)

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(
                when (channelId) {
                    CHANNEL_SYNC -> NotificationCompat.PRIORITY_LOW
                    else -> NotificationCompat.PRIORITY_HIGH
                }
            )
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createAllChannels(manager: NotificationManager) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        // Canal órdenes — HIGH
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ORDERS, CHANNEL_ORDERS_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Notificaciones de órdenes nuevas y completadas"
                enableVibration(true)
                enableLights(true)
            }
        )

        // Canal sync — NORMAL (no molesta)
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_SYNC, CHANNEL_SYNC_NAME, NotificationManager.IMPORTANCE_LOW).apply {
                description = "Notificaciones de sincronización en segundo plano"
                enableVibration(false)
            }
        )

        // Canal alertas — HIGH
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_ALERTS, CHANNEL_ALERTS_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Alertas operacionales urgentes"
                enableVibration(true)
                enableLights(true)
            }
        )
    }
}
