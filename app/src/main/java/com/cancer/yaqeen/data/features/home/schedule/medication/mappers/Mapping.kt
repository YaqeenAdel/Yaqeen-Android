package com.cancer.yaqeen.data.features.home.schedule.medication.mappers

import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.features.home.schedule.medication.responses.AddMedicationResponse


class MappingAddMedicationRemoteAsUIModel :
    Mapper<AddMedicationResponse, Boolean> {
    override fun map(input: AddMedicationResponse): Boolean {
        return input.response != null
    }
}
