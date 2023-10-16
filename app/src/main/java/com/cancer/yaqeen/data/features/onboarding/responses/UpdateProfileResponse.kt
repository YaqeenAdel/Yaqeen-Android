package com.cancer.yaqeen.data.features.onboarding.responses

import com.google.gson.annotations.SerializedName

data class UpdateProfileResponse (
    @SerializedName("update_Patients_many")
    val updatePatientResponse: List<UpdateResponse>?,

    @SerializedName("update_Doctors_many")
    val updateDoctorResponse: UpdateResponse?,

    @SerializedName("update_Users_many")
    val updateUserResponse: UpdateResponse?
)
data class UpdateResponse (
    val returning: List<UpdateReturningResponse>?
)
data class UpdatePatientResponse (
    val returning: List<UpdatePatientReturningResponse>?
)

class UpdateReturningResponse
data class UpdatePatientReturningResponse (
    @SerializedName("CancerTypeId")
    val cancerTypeID: Long?,

    @SerializedName("AgeGroup")
    val ageGroup: Long?,

    @SerializedName("CancerStageId")
    val cancerStageID: Long?
)
data class UpdateDoctorResponse (
    val returning: List<UpdateDoctorReturningResponse>?
)

data class UpdateDoctorReturningResponse (
    @SerializedName("University")
    val university: String?,

    @SerializedName("Degree")
    val degree: String?,

    @SerializedName("MedicalField")
    val medicalField: String?
)
//data class UpdateUserResponse (
//    val returning: List<UpdateDoctorReturningResponse>
//)

