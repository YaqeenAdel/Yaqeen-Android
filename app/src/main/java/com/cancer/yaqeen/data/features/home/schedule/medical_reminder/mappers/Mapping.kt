package com.cancer.yaqeen.data.features.home.schedule.medical_reminder.mappers

import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models.AddMedicalReminder
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models.MedicalReminder
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.responses.AddMedicalReminderResponse
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.responses.AddSymptomToMedicalReminderResponse
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.responses.GetMedicalRemindersResponse
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.responses.SchedulesResponse
import com.cancer.yaqeen.presentation.ui.main.treatment.getReminderTimeFromCronExpression


class MappingAddMedicalReminderRemoteAsUIModel :
    Mapper<AddMedicalReminderResponse, AddMedicalReminder?> {
    override fun map(input: AddMedicalReminderResponse): AddMedicalReminder? {
        return input.insertSchedulesOne?.run {
            AddMedicalReminder(
                scheduleID = scheduleID ?: 0
            )
        }
    }
}

class MappingAddSymptomToMedicalReminderRemoteAsUIModel(private val scheduleID: Int) :
    Mapper<AddSymptomToMedicalReminderResponse, AddMedicalReminder?> {
    override fun map(input: AddSymptomToMedicalReminderResponse): AddMedicalReminder? {
        return input.insertScheduleSymptomOne?.run {
            AddMedicalReminder(
                scheduleID = scheduleID,
                symptomIsAdded = true
            )
        }
    }
}

class MappingDeleteScheduleRemoteAsUIModel :
    Mapper<Any, Boolean> {
    override fun map(input: Any): Boolean {
        return input!= null
    }
}


class MappingMedicalRemindersRemoteAsModel: Mapper<GetMedicalRemindersResponse, List<MedicalReminder>> {
    override fun map(input: GetMedicalRemindersResponse): List<MedicalReminder> = input.schedules?.map {
        it.run {
            MedicalReminder(
                id = scheduleID ?: 0,
                doctorName = entity?.physician ?: "",
                location = entity?.location ?: "",
                phoneNumber = entity?.phoneNumber ?: "",
                startDate = "",
                reminderTime = "",
                reminderBeforeTime = "",
                notes = entity?.notes ?: "",
                symptom = null,
            )
        }
    } ?: listOf()
}
