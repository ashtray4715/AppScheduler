package com.ashtray.appscheduler.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MyTaskEntity::class], version = 1)
abstract class MyTaskDatabase: RoomDatabase() {

    companion object {
        private const val DATABASE_NAME = "task_list_db"

        @Volatile
        private var INSTANCE: MyTaskDatabase? = null

        fun getInstance(context: Context): MyTaskDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MyTaskDatabase::class.java,
                        DATABASE_NAME
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }

    abstract fun getDao(): MyTaskDao
}