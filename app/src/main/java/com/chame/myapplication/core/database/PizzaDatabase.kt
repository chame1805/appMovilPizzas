package com.chame.myapplication.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.chame.myapplication.core.database.dao.OrderDao
import com.chame.myapplication.core.database.dao.ProductDao
import com.chame.myapplication.core.database.dao.UserDao
import com.chame.myapplication.core.database.dao.WaiterLocationDao
import com.chame.myapplication.core.database.entity.OrderEntity
import com.chame.myapplication.core.database.entity.ProductEntity
import com.chame.myapplication.core.database.entity.UserEntity
import com.chame.myapplication.core.database.entity.WaiterLocationEntity

@Database(
    entities = [
        OrderEntity::class,
        UserEntity::class,
        ProductEntity::class,
        WaiterLocationEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class PizzaDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun waiterLocationDao(): WaiterLocationDao
}
