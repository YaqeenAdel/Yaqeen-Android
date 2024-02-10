package com.cancer.yaqeen.data.features.auth.requests

import com.google.gson.annotations.SerializedName

data class RefreshTokenRequestBody(
    @SerializedName("grant_type")
    val grantType: String,
    @SerializedName("client_id")
    val clientId: String,
//    @SerializedName("client_secret")
//    val clientSecret: String,
    @SerializedName("refresh_token")
    val refreshToken: String
)
