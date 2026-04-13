package com.chame.myapplication.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create products table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS products (
                id INTEGER PRIMARY KEY NOT NULL,
                name TEXT NOT NULL,
                price REAL NOT NULL,
                description TEXT,
                imageUrl TEXT,
                syncedAt INTEGER NOT NULL,
                updatedAt INTEGER NOT NULL
            )
        """.trimIndent())

        // Create waiter_locations table
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS waiter_locations (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                waiterId TEXT NOT NULL,
                latitude REAL NOT NULL,
                longitude REAL NOT NULL,
                timestamp INTEGER NOT NULL,
                synced INTEGER NOT NULL
            )
        """.trimIndent())

        // Add indexes
        database.execSQL("CREATE INDEX IF NOT EXISTS index_waiter_locations_waiterId_timestamp ON waiter_locations(waiterId, timestamp)")

        // Alter orders table to add sync fields
        database.execSQL("ALTER TABLE orders ADD COLUMN syncStatus TEXT NOT NULL DEFAULT 'SYNCED'")
        database.execSQL("ALTER TABLE orders ADD COLUMN serverUpdatedAt INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE orders ADD COLUMN localUpdatedAt INTEGER NOT NULL DEFAULT ${System.currentTimeMillis()}")
        database.execSQL("ALTER TABLE orders ADD COLUMN conflictResolved INTEGER NOT NULL DEFAULT 1")
    }
}
