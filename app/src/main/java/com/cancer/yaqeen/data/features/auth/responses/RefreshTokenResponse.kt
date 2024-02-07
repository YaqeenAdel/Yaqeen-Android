package com.cancer.yaqeen.data.features.auth.responses

data class RefreshTokenResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val refreshToken: String
)
