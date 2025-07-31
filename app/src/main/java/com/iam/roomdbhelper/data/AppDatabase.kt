package com.iam.roomdbhelper.data


import androidx.room.Database
import com.iam.roomdbhelper.BaseDatabase

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : BaseDatabase() {
    abstract fun userDao(): UserDao
}
