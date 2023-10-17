package com.cancer.yaqeen.data.network.apis

import com.cancer.yaqeen.data.features.auth.requests.LoginRequestBody
import com.cancer.yaqeen.data.features.auth.responses.LoginRemote
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateInterestsUserRequestBody
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateProfileRequestBody
import com.cancer.yaqeen.data.features.onboarding.responses.ResourcesResponse
import com.cancer.yaqeen.data.features.onboarding.responses.UpdateProfileResponse
import com.cancer.yaqeen.data.features.onboarding.responses.UserProfileResponse
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

    @POST("me/interests")
    suspend fun updateInterestsUser(
        @Body requestBody: UpdateInterestsUserRequestBody
    ): Response<Any>

    @PATCH("me")
    suspend fun updateUserProfile(
        @Body requestBody: UpdateProfileRequestBody
    ): Response<Any>

    @GET("me")
    suspend fun getUserProfile(): Response<UserProfileResponse>

}