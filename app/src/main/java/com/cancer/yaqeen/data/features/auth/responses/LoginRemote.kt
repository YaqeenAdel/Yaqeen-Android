package com.cancer.yaqeen.data.features.auth.responses

import com.google.gson.annotations.SerializedName

data class LoginRemote(
    @SerializedName("tokenString")
    val token: String,
    @SerializedName("refreshToken")
    val refreshToken: String,
    @SerializedName("user")
    val user: UserRemote
)

data class UserRemote(
    var fullName: String,
    var email: String = "",
    var mobileNumber: String,
    val userName: String
)
