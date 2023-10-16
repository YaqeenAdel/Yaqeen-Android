package com.cancer.yaqeen.data.features.auth.models


data class User(
    val id: String? = null,
    val name: String? = null,
    val nickname: String? = null,
    val pictureURL: String? = null,
    val familyName: String? = null,

    val gender: String? = null,
    val doctor: Doctor? = null,
    val agreedTerms: Boolean? = null,
    val isEmailVerified: Boolean? = null,
    val questionsAggregate: SAggregate? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val patient: Patient? = null,
    val email: String? = null
)
data class Doctor (
    val medicalField: String? = null,
    val degree: String? = null,
    val university: String? = null,
    val verificationStatus: VerificationStatus? = null,
    val answersAggregate: SAggregate? = null
)
data class VerificationStatus (
    val notes: String? = null,
    val verifierUserID: String? = null
)

data class SAggregate (
    val aggregate: Aggregate? = null
)
data class Aggregate (
    val count: Int? = null
)
data class Patient (
    val cancerTypeID: Int? = null,
    val ageGroup: Int? = null,
    val cancerStageID: Int? = null
)