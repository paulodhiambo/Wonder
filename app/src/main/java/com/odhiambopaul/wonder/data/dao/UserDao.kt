package com.odhiambopaul.wonder.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.odhiambopaul.wonder.data.entity.User
import io.reactivex.Completable
import io.reactivex.Flowable

@Dao
interface UserDao {
    @Insert
    fun saveUser(user: User): Completable

    @Query("SELECT * FROM User")
    fun getUsers(): Flowable<List<User>>

    @Query("DELETE FROM User")
    fun deleteAllUsers(): Completable
}