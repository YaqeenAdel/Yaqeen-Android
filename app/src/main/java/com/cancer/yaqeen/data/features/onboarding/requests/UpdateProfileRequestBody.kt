package com.cancer.yaqeen.data.features.onboarding.requests

import com.cancer.yaqeen.data.features.auth.models.UserType
import com.google.gson.annotations.SerializedName

data class UpdateProfileRequestBody(
//    @SerializedName("AgreedTerms")
//    val agreedTerms: Boolean,

    val doctor: Any?,

    val patient: Any?,

    val user: Any?,
//
//    @SerializedName("Questions_aggregate")
//    val questionsAggregate: SAggregate?
)
class UserRequestBody

data class DoctorRequestBody (
//    @SerializedName("Answers_aggregate")
//    val answersAggregate: SAggregate? = null,

    @SerializedName("Degree")
    val degree: String = "",

    @SerializedName("MedicalField")
    val medicalField: String = "",

    @SerializedName("University")
    val university: String = "",

//    @SerializedName("VerificationStatus")
//    val verificationStatus: VerificationStatus? = null
)

data class SAggregateRequestBody (
    val aggregate: AggregateRequestBody
)

data class AggregateRequestBody (
    val count: Int
)

data class VerificationStatusRequestBody (
    @SerializedName("Notes")
    val notes: String,

    @SerializedName("VerifierUserId")
    val verifierUserID: String
)

data class PatientRequestBody (
    @SerializedName("AgeGroup")
    val ageGroup: Int = 0,

    @SerializedName("CancerStageId")
    val cancerStageID: Int = 0,

    @SerializedName("CancerTypeId")
    val cancerTypeID: Int = 0
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
//            agreedTerms = agreedTerms,
            doctor =
            if (!isPatient)
                DoctorRequestBody(
//                    answersAggregate = aggregateCountDoctor?.let {
//                        SAggregate(
//                            Aggregate(
//                                count = aggregateCountDoctor
//                            )
//                        )
//                    },
                    degree = degreeIDDoctor.toString(),
                    medicalField = medicalFieldIDDoctor.toString(),
                    university = universityIDDoctor.toString(),
//                    verificationStatus = verifierUserIDDoctor?.let {
//                        VerificationStatus(
//                            notes = verificationNotesDoctor ?: "",
//                            verifierUserID = verifierUserIDDoctor.toString(),
//                        )
//                    },
                )
            else UserRequestBody(),
            patient =
            if (isPatient)
                PatientRequestBody(
                    ageGroup = ageGroupPatient ?: 0,
                    cancerStageID = cancerStageIDPatient ?: 0,
                    cancerTypeID = cancerTypeIDPatient ?: 0,
                )
            else UserRequestBody(),
            user = UserRequestBody()
//            questionsAggregate = questionsAggregateCount?.let {
//                SAggregate(
//                    aggregate = Aggregate(
//                        count = questionsAggregateCount
//                    )
//                )
//            }
        )

}
