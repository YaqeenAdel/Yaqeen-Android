package com.cancer.yaqeen.data.features.auth.models

import java.util.Date


data class User(
    val id: String?,
    val name: String?,
    val nickname: String?,
    val pictureURL: String?,
    val email: String?,
    val isEmailVerified: Boolean?,
    val familyName: String?,
    val createdAt: Date?,
//    var fullName: String?,
//    var mobileNumber: String?,
//    val userName: String?
)
