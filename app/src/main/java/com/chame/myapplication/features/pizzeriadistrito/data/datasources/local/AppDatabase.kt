package com.chame.myapplication.features.pizzeriadistrito.data.datasources.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [OrderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    
    companion object {
        const val DATABASE_NAME = "pizzeria_db"
    }
}
