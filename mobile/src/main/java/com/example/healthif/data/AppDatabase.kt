package com.example.healthif.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.example.healthif.data.DAO.HeartBeatsDao
import com.example.healthif.data.Entities.HeartBeat

@Database(
    entities = [HeartBeat::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase(){
    abstract fun heartBeatDao(): HeartBeatsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){

                INSTANCE ?: Room.databaseBuilder<AppDatabase>(
                    context = context.applicationContext,
                    name = "batimentos_database"
                )
                    .setDriver(AndroidSQLiteDriver())
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}


