package com.cancer.yaqeen.data.features.auth.models


data class User(
    val id: String,
    val name: String,
    val nickname: String,
    val pictureURL: String,
    val familyName: String,

    val gender: String,
    val doctor: Doctor? = null,
    val agreedTerms: Boolean,
//    val isEmailVerified: Boolean? = null,
//    val questionsAggregate: SAggregate? = null,
    val firstName: String,
    val lastName: String,
    val patient: Patient? = null,
    val email: String,
    val userInterests: List<UserInterest>,
)

data class UserInterest(
    val id: Int,
    val name: String
)

data class Doctor (
    val credentialsAttachments: List<String>,
    val degree: String,
    val medicalField: String,
    val university: String,
    val verificationStatus: String,
//    val answersAggregate: SAggregate? = null
)
//data class VerificationStatus (
//    val notes: String? = null,
//    val verifierUserID: String? = null
//)

//data class SAggregate (
//    val aggregate: Aggregate? = null
//)
//data class Aggregate (
//    val count: Int? = null
//)
data class Patient (
    val ageGroup: Int,
    val cancerStageID: Int,
    val cancerTypeID: Int,
    val stageName: String,
    val cancerTypeName: String,
)