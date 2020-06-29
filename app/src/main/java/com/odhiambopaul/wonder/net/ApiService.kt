package com.odhiambopaul.wonder.net

import com.odhiambopaul.wonder.data.entity.User
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiService @Inject constructor(private val retrofit: Retrofit) : ApiClient {
    private val api: ApiClient by lazy {
        retrofit.create(ApiClient::class.java)
    }

    override fun getUsers(): Observable<List<User>> {
        return api.getUsers()
    }

    override fun uploadUsers(
        name: RequestBody,
        gender: RequestBody,
        latitude: RequestBody,
        longitude: RequestBody,
        image: MultipartBody.Part?
    ): Observable<ResponseBody?>? {
        return api.uploadUsers(name, gender, latitude, longitude, image)
    }
}