package com.chame.myapplication.core.di

import android.content.Context
import androidx.room.Room
import com.chame.myapplication.core.database.PizzaDatabase
import com.chame.myapplication.core.database.dao.OrderDao
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
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideOrderDao(db: PizzaDatabase): OrderDao = db.orderDao()
}
