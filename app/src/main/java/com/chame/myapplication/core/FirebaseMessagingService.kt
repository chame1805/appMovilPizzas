package com.chame.myapplication.core

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.chame.myapplication.core.notifications.NotificationManagerHelper
import com.chame.myapplication.core.notifications.NotificationChannels

@AndroidEntryPoint
class FirebaseMessagingService : FirebaseMessagingService() {
    
    @Inject
    lateinit var notificationManager: NotificationManagerHelper
    
    companion object {
        private const val TAG = "FirebaseMessaging"
    }
    
    override fun onCreate() {
        super.onCreate()
        // Crear canales de notificación
        NotificationChannels.createNotificationChannels(this)
    }
    
    /**
     * Se llamará cuando se reciba un mensaje desde Firebase Cloud Messaging
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "From: ${remoteMessage.from}")
        
        // Extraer datos del mensaje
        val data = remoteMessage.data
        val title = remoteMessage.notification?.title ?: data["title"] ?: "Pizzería Distrito"
        val body = remoteMessage.notification?.body ?: data["body"] ?: "Tienes una nueva notificación"
        val notificationType = data["type"] ?: "order"
        val orderId = data["order_id"] ?: ""
        
        Log.d(TAG, "Message title: $title")
        Log.d(TAG, "Message body: $body")
        Log.d(TAG, "Message type: $notificationType")
        
        // Mostrar notificación según el tipo
        when (notificationType) {
            "order" -> {
                notificationManager.showOrderNotification(
                    orderId = orderId.ifEmpty { System.currentTimeMillis().toString() },
                    title = title,
                    message = body
                )
            }
            "promotion" -> {
                notificationManager.showPromotionNotification(
                    promotionId = data["promotion_id"] ?: "",
                    title = title,
                    message = body
                )
            }
            "urgent" -> {
                notificationManager.showUrgentNotification(
                    alertId = data["alert_id"] ?: "",
                    title = title,
                    message = body
                )
            }
            else -> {
                // Notificación genérica
                notificationManager.showOrderNotification(
                    orderId = System.currentTimeMillis().toString(),
                    title = title,
                    message = body
                )
            }
        }
        
        // Opcional: Manejar payload personalizado
        handleCustomPayload(data)
    }
    
    /**
     * Se llamará cuando cambie el token de Firebase
     */
    override fun onNewToken(token: String) {
        Log.d(TAG, "New token: $token")
        
        // Enviar el nuevo token al backend para actualizar
        sendTokenToBackend(token)
    }
    
    /**
     * Envía el token a tu backend para guardarlo
     */
    private fun sendTokenToBackend(token: String) {
        // TODO: Implementar envío del token al backend
        // Ejemplo:
        // val call = apiService.sendToken(SendTokenRequest(token))
        // call.enqueue(object : Callback<Void> {
        //     override fun onResponse(call: Call<Void>, response: Response<Void>) {
        //         Log.d(TAG, "Token sent successfully")
        //     }
        //     override fun onFailure(call: Call<Void>, t: Throwable) {
        //         Log.e(TAG, "Error sending token", t)
        //     }
        // })
        Log.d(TAG, "TODO: Implement sendTokenToBackend($token)")
    }
    
    /**
     * Maneja payloads personalizados según el tipo de notificación
     */
    private fun handleCustomPayload(data: Map<String, String>) {
        when (data["action"]) {
            "open_order" -> {
                // Navegar a la pantalla de orden
                Log.d(TAG, "Action: Open order ${data["order_id"]}")
            }
            "open_promotion" -> {
                // Navegar a la pantalla de promoción
                Log.d(TAG, "Action: Open promotion ${data["promotion_id"]}")
            }
        }
    }
}
