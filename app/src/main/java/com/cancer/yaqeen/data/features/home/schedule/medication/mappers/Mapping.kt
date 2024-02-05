package com.cancer.yaqeen.data.features.home.schedule.medication.mappers

import android.content.Context
import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.models.MedicationTrack
import com.cancer.yaqeen.data.features.home.schedule.medication.responses.AddMedicationResponse
import com.cancer.yaqeen.data.features.home.schedule.medication.responses.EditMedicationResponse
import com.cancer.yaqeen.data.features.home.schedule.medication.responses.SchedulesResponse
import com.cancer.yaqeen.data.features.home.schedule.medication.responses.TodaySchedulesResponse
import com.cancer.yaqeen.presentation.ui.main.treatment.getMedicationType
import com.cancer.yaqeen.presentation.ui.main.treatment.getUnitType


class MappingAddMedicationRemoteAsUIModel :
    Mapper<AddMedicationResponse, Boolean> {
    override fun map(input: AddMedicationResponse): Boolean {
        return input.response != null
    }
}


class MappingMedicationsRemindersRemoteAsModel: Mapper<SchedulesResponse, List<Medication>> {
    override fun map(input: SchedulesResponse): List<Medication> = input.schedules?.map {
        it.run {
            Medication(
                id = scheduleID ?: 0,
                medicationName = entity?.name ?: "",
                medicationType = entity?.type ?: "",
                strengthAmount = entity?.strengthTimes ?: 0,
                unitType = entity?.unit ?: "",
                dosageAmount = entity?.dosageTimes ?: 0,
                notes = "",
                scheduleType = entityType ?: "",
                time = cronExpression ?: ""
            )
        }
    } ?: listOf()
}

class MappingMedicationAsMedicationTrack(val context: Context): Mapper<Medication, MedicationTrack> {
    override fun map(input: Medication): MedicationTrack = input.run {
        MedicationTrack(
            medicationType = getMedicationType(context, medicationType),
            medicationName = medicationName,
            unitType = getUnitType(context, unitType),
            strengthAmount = strengthAmount.toString(),
            dosageAmount = dosageAmount.toString(),
            periodTime = null,
            specificDays = null,
            startDate = null,
            reminderTime = null,
            notes = notes,
            editable = true,
            medicationId = id
        )
    }
}

class MappingEditMedicationRemoteAsUIModel :
    Mapper<EditMedicationResponse, Boolean> {
    override fun map(input: EditMedicationResponse): Boolean {
        return input.response != null
    }
}

class MappingMedicationsRemindersFromNowRemoteAsModel: Mapper<TodaySchedulesResponse, List<Medication>> {
    override fun map(input: TodaySchedulesResponse): List<Medication> = input.schedules?.map {
        it.run {
            Medication(
                id = scheduleID ?: 0,
                medicationName = entity?.name ?: "",
                medicationType = entity?.type ?: "",
                strengthAmount = entity?.strengthTimes ?: 0,
                unitType = entity?.unit ?: "",
                dosageAmount = entity?.dosageTimes ?: 0,
                notes = "",
                scheduleType = "",
                time = ""
            )
        }
    } ?: listOf()
}


