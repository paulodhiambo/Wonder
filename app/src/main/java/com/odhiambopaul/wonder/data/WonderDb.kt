package com.odhiambopaul.wonder.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.odhiambopaul.wonder.data.dao.UserDao
import com.odhiambopaul.wonder.data.entity.User

@Database(entities = [User::class], version = 2)
abstract class WonderDb : RoomDatabase() {
    abstract fun userDao(): UserDao
}