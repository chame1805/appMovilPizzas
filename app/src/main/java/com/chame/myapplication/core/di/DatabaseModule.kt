package com.chame.myapplication.core.di

import android.content.Context
import androidx.room.Room
import com.chame.myapplication.features.pizzeriadistrito.data.datasources.local.AppDatabase
import com.chame.myapplication.features.pizzeriadistrito.data.datasources.local.OrderDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }
    
    @Provides
    @Singleton
    fun provideOrderDao(database: AppDatabase): OrderDao {
        return database.orderDao()
    }
}
