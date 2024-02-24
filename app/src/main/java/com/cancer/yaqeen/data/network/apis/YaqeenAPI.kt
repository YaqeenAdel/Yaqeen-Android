package com.cancer.yaqeen.data.network.apis

import com.cancer.yaqeen.data.features.home.articles.requests.BookmarkArticleRequest
import com.cancer.yaqeen.data.features.home.articles.responses.BookmarkArticleResponse
import com.cancer.yaqeen.data.features.home.articles.responses.BookmarkedArticlesResponse
import com.cancer.yaqeen.data.features.home.articles.responses.HomeArticlesResponse
import com.cancer.yaqeen.data.features.home.articles.responses.UnBookmarkArticleResponse
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateInterestsUserRequestBody
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateProfileRequestBody
import com.cancer.yaqeen.data.features.onboarding.responses.ResourcesResponse
import com.cancer.yaqeen.data.features.onboarding.responses.UniversitiesResponse
import com.cancer.yaqeen.data.features.onboarding.responses.UserProfileResponse
import com.cancer.yaqeen.data.features.home.schedule.medication.requests.AddMedicationRequest
import com.cancer.yaqeen.data.features.home.schedule.medication.responses.AddMedicationResponse
import com.cancer.yaqeen.data.features.home.schedule.medication.responses.EditMedicationResponse
import com.cancer.yaqeen.data.features.home.schedule.medication.responses.SchedulesResponse
import com.cancer.yaqeen.data.features.home.schedule.medication.responses.TodaySchedulesResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.AddSymptomRequest
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.UploadUrlRequest
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.AddSymptomResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.DeleteSymptomResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.EditSymptomResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.SymptomTypesResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.SymptomsResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.UploadUrlResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface YaqeenAPI {

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
    suspend fun getHomeArticles(
        @Query("lang") lang: String,
        @Query("search") searchQuery: String,
    ): Response<HomeArticlesResponse>

    @GET("bookmarks")
    suspend fun getBookmarkedArticles(): Response<BookmarkedArticlesResponse>

    @POST("bookmarks")
    suspend fun bookmarkArticle(
        @Body requestBody: BookmarkArticleRequest
    ): Response<BookmarkArticleResponse>

    @DELETE("bookmarks/{bookmarkId}")
    suspend fun unBookmarkArticle(
        @Path("bookmarkId") bookmarkId: Int
    ): Response<UnBookmarkArticleResponse>

    @POST("Schedules")
    suspend fun addMedication(
        @Body requestBody: AddMedicationRequest
    ): Response<AddMedicationResponse>

    @POST("Schedules/{scheduleId}")
    suspend fun editMedication(
        @Path("scheduleId") scheduleId: Int,
        @Body requestBody: AddMedicationRequest
    ): Response<EditMedicationResponse>

    @GET("Schedules/{scheduleType}")
    suspend fun getMedicationsReminders(
        @Path("scheduleType") scheduleType: String
    ): Response<SchedulesResponse>

    @GET("future_schedules")
    suspend fun getTodayReminders(): Response<TodaySchedulesResponse>

    @GET("resources/symptoms")
    suspend fun getSymptomsTypes(): Response<SymptomTypesResponse>

    @GET("Symptoms")
    suspend fun getSymptoms(): Response<SymptomsResponse>

    @PUT("upload_url")
    suspend fun createAnUploadLocation(
        @Body requestBody: UploadUrlRequest
    ): Response<UploadUrlResponse>

    @Multipart
    @PUT("upload_url")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part signedUrl: MultipartBody.Part,
    ): Response<Any>

    @POST("Symptoms")
    suspend fun addSymptom(
        @Body requestBody: AddSymptomRequest
    ): Response<AddSymptomResponse>

    @POST("Symptoms/{symptomId}")
    suspend fun editSymptom(
        @Path("symptomId") symptomId: Int,
        @Body requestBody: AddSymptomRequest
    ): Response<EditSymptomResponse>

    @DELETE("Symptoms/{symptomId}")
    suspend fun deleteSymptom(
        @Path("symptomId") symptomId: Int
    ): Response<DeleteSymptomResponse>

}