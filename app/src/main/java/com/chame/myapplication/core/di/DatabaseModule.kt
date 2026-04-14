package com.chame.myapplication.core.di

import android.content.Context
import androidx.room.Room
import com.chame.myapplication.core.database.PizzaDatabase
import com.chame.myapplication.core.database.dao.OrderDao
import com.chame.myapplication.core.database.dao.WaiterLocationDao
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
    fun provideDatabase(@ApplicationContext context: Context): PizzaDatabase =
        Room.databaseBuilder(context, PizzaDatabase::class.java, "pizza_db")
            .addMigrations(PizzaDatabase.MIGRATION_1_2)
            .build()

    @Provides
    @Singleton
    fun provideOrderDao(db: PizzaDatabase): OrderDao = db.orderDao()

    @Provides
    @Singleton
    fun provideWaiterLocationDao(db: PizzaDatabase): WaiterLocationDao = db.waiterLocationDao()
}
