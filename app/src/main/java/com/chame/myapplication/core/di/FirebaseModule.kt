package com.chame.myapplication.core.di

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import com.chame.myapplication.core.notifications.NotificationManagerHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    
    @Provides
    @Singleton
    fun provideFirebaseMessaging(): FirebaseMessaging {
        return FirebaseMessaging.getInstance()
    }
    
    @Provides
    @Singleton
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManagerHelper {
        return NotificationManagerHelper(context)
    }
}
