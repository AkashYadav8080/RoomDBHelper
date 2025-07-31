package com.iam.roomdbhelper.repository

import android.content.Context
import com.iam.roomdbhelper.RoomDatabaseBuilder
import com.iam.roomdbhelper.data.AppDatabase
import com.iam.roomdbhelper.data.User

class UserRepository(context: Context) {

    private val userDao = RoomDatabaseBuilder

        .getDatabase(context, AppDatabase::class.java, "user_db")
        .userDao()

    suspend fun insertUser(user: User) {
        userDao.insert(user)
    }

    suspend fun getUsers(): List<User> {
        return userDao.getAllUsers()
    }
}
