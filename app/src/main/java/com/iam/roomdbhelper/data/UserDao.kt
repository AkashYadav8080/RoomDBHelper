package com.iam.roomdbhelper.data

import androidx.room.Dao
import androidx.room.Query
import com.iam.roomdbhelper.BaseDao

@Dao
interface UserDao : BaseDao<User> {

    @Query("SELECT * FROM User")
    suspend fun getAllUsers(): List<User>
}
