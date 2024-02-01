package com.cancer.yaqeen.data.features.onboarding.responses

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("Users")
    val users: List<UserResponse>?
)

data class UserResponse (
    @SerializedName("AgreedTerms")
    val agreedTerms: Boolean?,

    @SerializedName("Email")
    val email: String?,

    @SerializedName("FirstName")
    val firstName: String?,

    @SerializedName("Gender")
    val gender: String?,

    @SerializedName("LastName")
    val lastName: String?,

    @SerializedName("UserId")
    val userID: String?,

    @SerializedName("InterestUsers")
    val interestUsers: List<InterestUserResponse>?,

    @SerializedName("Doctor")
    val doctor: DoctorResponse?,

    @SerializedName("Patient")
    val patient: PatientResponse?,

//    @SerializedName("IsEmailVerified")
//    val isEmailVerified: Boolean?,
//
//    @SerializedName("Questions_aggregate")
//    val questionsAggregate: SAggregateResponse?,
)
data class InterestUserResponse (
    @SerializedName("InterestsInterestId")
    val interestID: Int?,

    @SerializedName("Interest")
    val interest: InterestUserTranslationsResponse?
)

data class InterestUserTranslationsResponse (
    @SerializedName("Translations")
    val translations: List<InterestTranslationResponse>?
)

data class InterestTranslationResponse (
    @SerializedName("Translation")
    val translation: TranslationResponse?,

    @SerializedName("Language")
    val language: String?
)

data class TranslationResponse (
    @SerializedName("Name")
    val name: String?
)


data class DoctorResponse (
    @SerializedName("CredentialsAttachments")
    val credentialsAttachments: List<String>?,

    @SerializedName("Degree")
    val degree: String?,

    @SerializedName("DeletedAt")
    val deletedAt: Any? = null,

    @SerializedName("MedicalField")
    val medicalField: String?,

    @SerializedName("University")
    val university: String?,

    @SerializedName("VerificationStatus")
    val verificationStatus: String?,

//    @SerializedName("Answers_aggregate")
//    val answersAggregate: SAggregateResponse?
)

//data class VerificationStatusResponse (
//    val notes: String?,
//    val verifierUserID: String?
//)
//
//data class SAggregateResponse (
//    val aggregate: AggregateResponse?
//)
//
//
//data class AggregateResponse (
//    val count: Int?
//)


data class PatientResponse (
    @SerializedName("AgeGroup")
    val ageGroup: Int?,

    @SerializedName("CancerStageId")
    val cancerStageID: Int?,

    @SerializedName("CancerTypeId")
    val cancerTypeID: Int?,

    @SerializedName("CancerStage")
    val cancerStage: CancerStagePatientResponse?,

    @SerializedName("CancerType")
    val cancerType: CancerTypePatientResponse?
)

data class CancerStagePatientResponse (
    @SerializedName("Translations")
    val translations: List<CancerStagePatientTranslationResponse>?
)

data class CancerStagePatientTranslationResponse (
    @SerializedName("Translation")
    val translation: CancerSTranslationResponse?
)

data class CancerSTranslationResponse (
    @SerializedName("StageName")
    val stageName: String?
)

data class CancerTypePatientResponse (
    @SerializedName("Translations")
    val translations: List<CancerTypePatientTranslationResponse>?
)


data class CancerTypePatientTranslationResponse (
    @SerializedName("Translation")
    val translation: CancerTTranslationResponse?
)


data class CancerTTranslationResponse (
    @SerializedName("CancerTypeName")
    val cancerTypeName: String?
)
