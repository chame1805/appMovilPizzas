package com.chame.myapplication.core.di

import android.content.Context
import androidx.room.Room
import com.chame.myapplication.core.database.PizzaDatabase
import com.chame.myapplication.core.database.dao.OrderDao
import com.chame.myapplication.core.database.dao.ProductDao
import com.chame.myapplication.core.database.dao.UserDao
import com.chame.myapplication.core.database.dao.WaiterLocationDao
import com.chame.myapplication.core.database.migrations.MIGRATION_1_2
import com.chame.myapplication.core.database.migrations.MIGRATION_2_3
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
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideOrderDao(db: PizzaDatabase): OrderDao = db.orderDao()

    @Provides
    @Singleton
    fun provideUserDao(db: PizzaDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun provideProductDao(db: PizzaDatabase): ProductDao = db.productDao()

    @Provides
    @Singleton
    fun provideWaiterLocationDao(db: PizzaDatabase): WaiterLocationDao =
        db.waiterLocationDao()
}
