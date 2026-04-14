package com.chame.myapplication.core.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.chame.myapplication.R
import com.chame.myapplication.core.notifications.PizzaFirebaseMessagingService
import com.chame.myapplication.core.websocket.KitchenWebSocketManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class KitchenOrderForegroundService : Service() {

    @Inject
    lateinit var kitchenWebSocketManager: KitchenWebSocketManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, KitchenOrderForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, KitchenOrderForegroundService::class.java))
        }
    }

    override fun onCreate() {
        super.onCreate()
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
        kitchenWebSocketManager.connect()
        listenForNewOrders()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // Si el sistema mata el servicio, lo reinicia automáticamente
    }

    override fun onDestroy() {
        kitchenWebSocketManager.disconnect()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun listenForNewOrders() {
        serviceScope.launch {
            kitchenWebSocketManager.events.collect { event ->
                if (event.event == "NEW_ORDER" || event.event == "ORDER_CREATED") {
                    showNewOrderNotification(event.pizzaName, event.clientName, event.tableNumber)
                }
            }
        }
    }

    private fun showNewOrderNotification(pizzaName: String, clientName: String, tableNumber: Int) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PizzaFirebaseMessagingService.CHANNEL_ID,
                PizzaFirebaseMessagingService.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            manager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, PizzaFirebaseMessagingService.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Nueva orden recibida")
            .setContentText("$pizzaName — Mesa $tableNumber ($clientName)")
            .setAutoCancel(true)
            .build()

        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun buildNotification() = run {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PizzaFirebaseMessagingService.CHANNEL_ID,
                PizzaFirebaseMessagingService.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }

        NotificationCompat.Builder(this, PizzaFirebaseMessagingService.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Cocina activa")
            .setContentText("Escuchando nuevas órdenes...")
            .setOngoing(true)
            .build()
    }
}
