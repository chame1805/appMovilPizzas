package com.chame.myapplication.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chame.myapplication.core.database.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: String): UserEntity?

    @Query("SELECT * FROM users LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Query("DELETE FROM users")
    suspend fun clearAll()

    @Query("UPDATE users SET token = :token, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateToken(id: String, token: String, updatedAt: Long)
}
