package com.cancer.yaqeen.data.features.home.schedule.medication.requests

import com.cancer.yaqeen.data.features.onboarding.requests.UpdateProfileRequestBody
import com.cancer.yaqeen.data.utils.tryToLong
import com.google.gson.annotations.SerializedName

data class AddMedicationRequest(
    val schedule: ScheduleMedicationRequest
)
data class ScheduleMedicationRequest (
    @SerializedName("EntityType")
    val entityType: String,

    @SerializedName("Entity")
    val entity: EntityMedicationRequest,

    @SerializedName("CronExpression")
    val cronExpression: String
)

data class EntityMedicationRequest (
    @SerializedName("Name")
    val name: String,

    @SerializedName("Type")
    val type: String,

    @SerializedName("Unit")
    val unit: String,

    @SerializedName("StrengthTimes100")
    val strengthTimes: Long,

    @SerializedName("DosageTimes100")
    val dosageTimes: Long
)


data class AddMedicationRequestBuilder(
    val medicationName: String,
    val medicationTypeName: String,
    val unitTypeName: String,
    val strengthAmount: String,
    val dosageAmount: String,
    val cronExpression: String
) {
    fun buildRequestBody(): AddMedicationRequest =
        AddMedicationRequest(
            schedule = ScheduleMedicationRequest(
                entityType = "medication",
                entity = EntityMedicationRequest(
                    name = medicationName,
                    type = medicationTypeName,
                    unit = unitTypeName,
                    strengthTimes = strengthAmount.tryToLong(),
                    dosageTimes = dosageAmount.tryToLong(),
                ),
                cronExpression = cronExpression
            )
        )
}