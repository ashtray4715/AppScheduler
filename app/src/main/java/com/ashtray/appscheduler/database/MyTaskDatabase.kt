package com.ashtray.appscheduler.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ashtray.appscheduler.common.GPApplication

@Database(entities = [MyTaskEntity::class], version = 1)
abstract class MyTaskDatabase: RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "task_list_db"

        private val database: MyTaskDatabase by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            Room.databaseBuilder(
                GPApplication.getInstance(),
                MyTaskDatabase::class.java,
                DATABASE_NAME
            ).build()
        }

        fun getInstance(): MyTaskDatabase {
            return database
        }
    }

    abstract fun getDao(): MyTaskDao
}