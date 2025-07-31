package com.iam.roomdbhelper

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

object RoomDatabaseBuilder {

    private val instances = mutableMapOf<String, RoomDatabase>()

    @Synchronized
    fun <T : RoomDatabase> getDatabase(
        context: Context,
        dbClass: Class<T>,
        dbName: String
    ): T {
        return instances.getOrPut(dbName) {
            Room.databaseBuilder(context.applicationContext, dbClass, dbName)
                .fallbackToDestructiveMigration()
                .build()
        } as T
    }
}
