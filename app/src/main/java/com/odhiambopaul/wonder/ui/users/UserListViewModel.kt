package com.odhiambopaul.wonder.ui.users

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.odhiambopaul.wonder.data.entity.User
import com.odhiambopaul.wonder.di.ui.BaseViewModel
import com.odhiambopaul.wonder.repository.DatabaseRepository
import com.odhiambopaul.wonder.repository.NetworkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UserListViewModel @Inject constructor(
    val networkRepository: NetworkRepository,
    private val databaseRepository: DatabaseRepository
) : BaseViewModel() {
    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>>
        get() = _users

    fun getUsers() {
        compositeDisposable.add(
            databaseRepository.getUsers()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { }
                .doOnTerminate { }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _users.value = it
                }, {
                    Log.e("Error::", it.localizedMessage!!)
                })
        )
    }
}