package com.odhiambopaul.wonder.net

import com.odhiambopaul.wonder.data.entity.User
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


interface ApiClient {
    companion object {
        const val UPLOAD_USERS = "users/create"
        const val GET_USERS = "users"
    }

    @GET(GET_USERS)
    fun getUsers(): Observable<List<User>>

    @Multipart
    @POST(UPLOAD_USERS)
    fun uploadUsers(
        @Part("name") name: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part image: MultipartBody.Part?
    ): Observable<ResponseBody?>?
}