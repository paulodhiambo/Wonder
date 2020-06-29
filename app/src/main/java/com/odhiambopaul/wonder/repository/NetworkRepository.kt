package com.odhiambopaul.wonder.repository

import com.odhiambopaul.wonder.net.ApiService
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import javax.inject.Inject

class NetworkRepository @Inject constructor(private val service: ApiService) {
    fun uploadUsers(
        name: RequestBody,
        gender: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody,
        image: MultipartBody.Part?
    ): Observable<ResponseBody?>? {
        return service.uploadUsers(name, gender, latitude, longitude, image)
    }
}