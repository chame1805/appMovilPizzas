package com.chame.myapplication.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chame.myapplication.core.database.entity.ProductEntity

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY name ASC")
    suspend fun getAll(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(product: ProductEntity)

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: Int): ProductEntity?

    @Query("DELETE FROM products")
    suspend fun clearAll()

    @Query("UPDATE products SET updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateTimestamp(id: Int, updatedAt: Long)
}
