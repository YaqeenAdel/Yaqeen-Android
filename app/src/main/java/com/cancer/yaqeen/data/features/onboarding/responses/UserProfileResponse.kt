package com.cancer.yaqeen.data.features.onboarding.responses

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("Users")
    val users: List<UserResponse>?
)

data class UserResponse (
    @SerializedName("Gender")
    val gender: String?,

    @SerializedName("Doctor")
    val doctor: DoctorResponse?,

    @SerializedName("AgreedTerms")
    val agreedTerms: Boolean?,

    @SerializedName("IsEmailVerified")
    val isEmailVerified: Boolean?,

    @SerializedName("Questions_aggregate")
    val questionsAggregate: SAggregateResponse?,

    @SerializedName("FirstName")
    val firstName: String?,

    @SerializedName("LastName")
    val lastName: String?,

    @SerializedName("Patient")
    val patient: PatientResponse?,

    @SerializedName("Email")
    val email: String?
)


data class DoctorResponse (
    @SerializedName("MedicalField")
    val medicalField: String?,

    @SerializedName("Degree")
    val degree: String?,

    @SerializedName("University")
    val university: String?,

    @SerializedName("VerificationStatus")
    val verificationStatus: VerificationStatusResponse?,

    @SerializedName("Answers_aggregate")
    val answersAggregate: SAggregateResponse?
)

data class VerificationStatusResponse (
    val notes: String?,
    val verifierUserID: String?
)

data class SAggregateResponse (
    val aggregate: AggregateResponse?
)


data class AggregateResponse (
    val count: Int?
)


data class PatientResponse (
    @SerializedName("CancerTypeId")
    val cancerTypeID: Int?,

    @SerializedName("AgeGroup")
    val ageGroup: Int?,

    @SerializedName("CancerStageId")
    val cancerStageID: Int?
)
