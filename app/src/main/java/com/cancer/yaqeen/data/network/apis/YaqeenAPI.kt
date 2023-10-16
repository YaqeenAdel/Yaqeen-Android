package com.cancer.yaqeen.data.network.apis

import com.cancer.yaqeen.data.features.auth.requests.LoginRequestBody
import com.cancer.yaqeen.data.features.auth.responses.LoginRemote
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateProfileRequestBody
import com.cancer.yaqeen.data.features.onboarding.responses.ResourcesResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST

interface YaqeenAPI {
    @POST("/api/MobileAuth/Login")
    suspend fun login(@Body requestBody: LoginRequestBody): Response<LoginRemote>

    @GET("resources")
    @Headers("isAuthorization: false")
    suspend fun getResources(): Response<ResourcesResponse>

    @PATCH("me")
    suspend fun updateUserProfile(
        @Body requestBody: UpdateProfileRequestBody
    ): Response<Boolean>

}