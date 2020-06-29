package com.odhiambopaul.wonder.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.odhiambopaul.wonder.data.entity.User
import com.odhiambopaul.wonder.di.ui.BaseViewModel
import com.odhiambopaul.wonder.repository.DatabaseRepository
import com.odhiambopaul.wonder.repository.NetworkRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    private val networkRepository: NetworkRepository,
    private val databaseRepository: DatabaseRepository
) : BaseViewModel() {
    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    fun saveUserLocal(user: User) {
        compositeDisposable.add(
            databaseRepository.saveUser(user)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { _status.value = "loading" }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _status.value = "success"
                }, {
                    _status.value = "fail"
                    Log.e("error::", it.localizedMessage!!)
                })
        )
    }

    fun uploadUsers(
        name: RequestBody,
        gender: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody,
        image: MultipartBody.Part?
    ) {
        compositeDisposable.add(
            networkRepository.uploadUsers(name, gender, latitude, longitude, image)!!
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { _status.value = "loading" }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    _status.value = "success"
                }, {
                    _status.value = "fail"
                    Log.e("error::", it.localizedMessage!!)
                })
        )
    }
}