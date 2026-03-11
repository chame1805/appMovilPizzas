package com.chame.myapplication.core.websocket

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.chame.myapplication.core.session.SessionManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

data class WaiterEvent(
    val event: String,
    val id: Int,
    val pizzaName: String = "",
    val tableNumber: Int = 0,
    val status: String = "",
    val updatedAt: String = ""
)

@Singleton
class WaiterWebSocketManager @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context
) {
    private var webSocket: WebSocket? = null
    private val gson = Gson()

    private val _events = MutableSharedFlow<WaiterEvent>(extraBufferCapacity = 20)
    val events: SharedFlow<WaiterEvent> = _events.asSharedFlow()

    fun connect() {
        disconnect()
        val token = sessionManager.token
        val waiterId = sessionManager.userId
        val request = Request.Builder()
            .url("ws://44.212.148.188:8000/orders/ws/waiter/$waiterId?token=$token")
            .build()

        webSocket = okHttpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onMessage(webSocket: WebSocket, text: String) {
                val json = gson.fromJson(text, JsonObject::class.java)
                val event = json.get("event")?.asString ?: return
                val id = json.get("id")?.asInt ?: return

                val waiterEvent = WaiterEvent(
                    event = event,
                    id = id,
                    pizzaName = json.get("pizza_name")?.asString ?: "",
                    tableNumber = json.get("table_number")?.asInt ?: 0,
                    status = json.get("status")?.asString ?: "",
                    updatedAt = json.get("updated_at")?.asString ?: ""
                )
                _events.tryEmit(waiterEvent)
                if (event == "ORDER_COMPLETED") {
                    vibrate()
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                // Connection failed
            }
        })
    }

    fun disconnect() {
        webSocket?.close(1000, "Closing")
        webSocket = null
    }

    private fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            val vibrator = vibratorManager?.defaultVibrator
            vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(500)
            }
        }
    }
}
