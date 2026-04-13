package com.chame.myapplication.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chame.myapplication.core.database.dao.OrderDao
import com.chame.myapplication.core.database.entity.OrderEntity

@Database(entities = [OrderEntity::class], version = 1, exportSchema = false)
abstract class PizzaDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
}
