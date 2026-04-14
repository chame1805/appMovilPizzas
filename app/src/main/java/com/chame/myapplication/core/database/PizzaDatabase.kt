package com.chame.myapplication.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chame.myapplication.core.database.dao.OrderDao
import com.chame.myapplication.core.database.dao.WaiterLocationDao
import com.chame.myapplication.core.database.entity.OrderEntity
import com.chame.myapplication.core.database.entity.WaiterLocationEntity

@Database(
    entities = [OrderEntity::class, WaiterLocationEntity::class],
    version = 2,
    exportSchema = false
)
abstract class PizzaDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao
    abstract fun waiterLocationDao(): WaiterLocationDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `waiter_locations` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `waiterId` INTEGER NOT NULL,
                        `latitude` REAL NOT NULL,
                        `longitude` REAL NOT NULL,
                        `accuracy` REAL NOT NULL,
                        `timestamp` INTEGER NOT NULL,
                        `syncStatus` TEXT NOT NULL DEFAULT 'PENDING'
                    )
                """.trimIndent())
                database.execSQL("""
                    CREATE INDEX IF NOT EXISTS `index_waiter_locations_waiterId_timestamp`
                    ON `waiter_locations` (`waiterId`, `timestamp`)
                """.trimIndent())
            }
        }
    }
}
