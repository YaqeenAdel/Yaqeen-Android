package com.cancer.yaqeen.data.network

import com.cancer.yaqeen.data.features.auth.requests.LoginRequestBody
import com.cancer.yaqeen.data.features.auth.responses.LoginRemote
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface YaqeenAPI {
    @POST("/api/MobileAuth/Login")
    suspend fun login(@Body requestBody: LoginRequestBody): Response<LoginRemote>

}