package com.odhiambopaul.wonder.repository

import com.odhiambopaul.wonder.data.WonderDb
import com.odhiambopaul.wonder.data.dao.UserDao
import com.odhiambopaul.wonder.data.entity.User
import io.reactivex.Completable
import io.reactivex.Flowable
import javax.inject.Inject

class DatabaseRepository @Inject constructor(wonderDb: WonderDb) {
    private var userDao: UserDao = wonderDb.userDao()

    fun saveUser(user: User): Completable {
        return userDao.saveUser(user)
    }

    fun getUsers(): Flowable<List<User>> {
        return userDao.getUsers()
    }
}