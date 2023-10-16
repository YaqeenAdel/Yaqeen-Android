package com.cancer.yaqeen.data.features.onboarding.requests

import com.cancer.yaqeen.data.features.auth.models.UserType
import com.google.gson.annotations.SerializedName

data class UpdateProfileRequestBody(
    @SerializedName("user")
    val user: UserRequestBody
)
data class UserRequestBody(
    @SerializedName("AgreedTerms")
    val agreedTerms: Boolean,

    @SerializedName("Doctor")
    val doctor: Doctor?,

//    @SerializedName("Email")
//    val email: String,
//
//    @SerializedName("FirstName")
//    val firstName: String,
//
//    @SerializedName("Gender")
//    val gender: String,
//
//    @SerializedName("IsEmailVerified")
//    val isEmailVerified: String,
//
//    @SerializedName("LastName")
//    val lastName: String,

    @SerializedName("Patient")
    val patient: Patient?,

    @SerializedName("Questions_aggregate")
    val questionsAggregate: SAggregate?
)

data class Doctor (
    @SerializedName("Answers_aggregate")
    val answersAggregate: SAggregate?,

    @SerializedName("Degree")
    val degree: String,

    @SerializedName("MedicalField")
    val medicalField: String,

    @SerializedName("University")
    val university: String,

    @SerializedName("VerificationStatus")
    val verificationStatus: VerificationStatus?
)

data class SAggregate (
    val aggregate: Aggregate
)

data class Aggregate (
    val count: Int
)

data class VerificationStatus (
    @SerializedName("Notes")
    val notes: String,

    @SerializedName("VerifierUserId")
    val verifierUserID: String
)

data class Patient (
    @SerializedName("AgeGroup")
    val ageGroup: Int?,

    @SerializedName("CancerStageId")
    val cancerStageID: Int,

    @SerializedName("CancerTypeId")
    val cancerTypeID: Int
)

data class UpdateProfileRequestBuilder(
    val isPatient: Boolean,
    val agreedTerms: Boolean,
    val aggregateCountDoctor: Int? = null,
    val degreeIDDoctor: String? = null,
    val medicalFieldIDDoctor: String? = null,
    val universityIDDoctor: String? = null,
    val verificationNotesDoctor: String? = null,
    val verifierUserIDDoctor: Int? = null,
    val ageGroupPatient: Int? = null,
    val cancerStageIDPatient: Int? = null,
    val cancerTypeIDPatient: Int? = null,
    val questionsAggregateCount: Int? = null
){
    fun buildRequestBody(): UpdateProfileRequestBody =
        UpdateProfileRequestBody(
            user = UserRequestBody(
                agreedTerms = agreedTerms,
                doctor =
                if (!isPatient)
                    Doctor(
                        answersAggregate = aggregateCountDoctor?.let {
                            SAggregate(
                                Aggregate(
                                    count = aggregateCountDoctor
                                )
                            )
                        },
                        degree = degreeIDDoctor.toString(),
                        medicalField = medicalFieldIDDoctor.toString(),
                        university = universityIDDoctor.toString(),
                        verificationStatus = verifierUserIDDoctor?.let {
                            VerificationStatus(
                                notes = verificationNotesDoctor ?: "",
                                verifierUserID = verifierUserIDDoctor.toString(),
                            )
                        },
                    )
                else null,
                patient =
                if (isPatient)
                    Patient(
                        ageGroup = ageGroupPatient,
                        cancerStageID = cancerStageIDPatient ?: 0,
                        cancerTypeID = cancerTypeIDPatient ?: 0,
                    )
                else null,
                questionsAggregate = questionsAggregateCount?.let {
                    SAggregate(
                        aggregate = Aggregate(
                            count = questionsAggregateCount
                        )
                    )
                }
            )
        )

}
