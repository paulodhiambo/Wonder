package com.odhiambopaul.wonder.di.module

import android.app.Application
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.odhiambopaul.wonder.data.WonderDb
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
class DataBaseModule(application: Application) {
    private var wonderApplication = application
    private lateinit var wonderDatabase: WonderDb

    private val databaseCallback = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.d("RoomDatabaseModule", "onCreate")
            CoroutineScope(Dispatchers.IO).launch {
                //call add sample users to db
            }
        }
    }

    @Singleton
    @Provides
    fun provideRoomDatabase(): WonderDb {
        wonderDatabase = Room.databaseBuilder(wonderApplication, WonderDb::class.java, "wonder.db")
            .fallbackToDestructiveMigration()
            .addCallback(databaseCallback)
            .build()
        return wonderDatabase
    }

    @Singleton
    @Provides
    fun providesUserDao(wonderDb: WonderDb) = wonderDb.userDao()
}