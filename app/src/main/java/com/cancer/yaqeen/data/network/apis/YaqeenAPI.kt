package com.cancer.yaqeen.data.network.apis

import com.cancer.yaqeen.data.features.auth.requests.LoginRequestBody
import com.cancer.yaqeen.data.features.auth.responses.LoginRemote
import com.cancer.yaqeen.data.features.home.responses.HomeArticlesResponse
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateInterestsUserRequestBody
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateProfileRequestBody
import com.cancer.yaqeen.data.features.onboarding.responses.ResourcesResponse
import com.cancer.yaqeen.data.features.onboarding.responses.UniversitiesResponse
import com.cancer.yaqeen.data.features.onboarding.responses.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface YaqeenAPI {
    @POST("/api/MobileAuth/Login")
    suspend fun login(@Body requestBody: LoginRequestBody): Response<LoginRemote>

    @GET("resources")
    @Headers("isAuthorization: false")
    suspend fun getResources(
        @Query("lang") lang: String
    ): Response<ResourcesResponse>

    @GET("universities/{CountryCode}/{StateCode}")
    @Headers("isAuthorization: false")
    suspend fun getUniversities(
        @Path("CountryCode") countryCode: String,
        @Path("StateCode") stateCode: String,
    ): Response<UniversitiesResponse>

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
    @GET("content/articles")
    @Headers("isAuthorization: false")
    suspend fun getHomeArticles(): Response<HomeArticlesResponse>

}