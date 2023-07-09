package com.cancer.yaqeen.data.features.auth.models


data class User(
    var fullName: String,
    var email: String = "",
    var mobileNumber: String,
    val userName: String
)
