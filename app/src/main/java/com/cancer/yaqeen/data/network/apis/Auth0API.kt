package com.cancer.yaqeen.data.network.apis

import com.cancer.yaqeen.data.features.auth.requests.RefreshTokenRequestBody
import com.cancer.yaqeen.data.features.auth.responses.RefreshTokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface Auth0API {
    @Headers("isAuth0: true")
    @POST("oauth/token")
    suspend fun refreshToken(
        @Body requestBody: RefreshTokenRequestBody
    ): Response<RefreshTokenResponse>

}