package com.chame.myapplication

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PizzaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializar Firebase automáticamente
        Firebase.initialize(this)
    }
}

