package com.cancer.yaqeen.data.features.home.schedule.medical_reminder.mappers

import android.content.Context
import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models.ModifyMedicalReminder
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models.MedicalReminder
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models.MedicalReminderTrack
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.responses.AddMedicalReminderResponse
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.responses.AddSymptomToMedicalReminderResponse
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.responses.DeleteSymptomFromScheduleResponse
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.responses.EditMedicalReminderResponse
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.responses.GetMedicalRemindersResponse
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.ReminderBefore
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingSymptomRemoteAsModel
import com.cancer.yaqeen.data.utils.convertDateTimeToMillis
import com.cancer.yaqeen.presentation.ui.main.treatment.getReminderTimeFromDateTime


class MappingAddMedicalReminderRemoteAsUIModel :
    Mapper<AddMedicalReminderResponse, ModifyMedicalReminder?> {
    override fun map(input: AddMedicalReminderResponse): ModifyMedicalReminder? {
        return input.insertSchedulesOne?.run {
            ModifyMedicalReminder(
                scheduleID = scheduleID ?: 0
            )
        }
    }
}
class MappingEditMedicalReminderRemoteAsUIModel :
    Mapper<EditMedicalReminderResponse, ModifyMedicalReminder?> {
    override fun map(input: EditMedicalReminderResponse): ModifyMedicalReminder? {
        return input.updateSchedulesOne?.run {
            ModifyMedicalReminder(
                scheduleID = scheduleID ?: 0
            )
        }
    }
}

class MappingAddSymptomToMedicalReminderRemoteAsUIModel(private val scheduleID: Int) :
    Mapper<AddSymptomToMedicalReminderResponse, ModifyMedicalReminder?> {
    override fun map(input: AddSymptomToMedicalReminderResponse): ModifyMedicalReminder? {
        return input.insertScheduleSymptomOne?.run {
            ModifyMedicalReminder(
                scheduleID = scheduleID,
                symptomIsAdded = true
            )
        }
    }
}

class MappingDeleteSymptomFromScheduleRemoteAsUIModel :
    Mapper<DeleteSymptomFromScheduleResponse, Boolean> {
    override fun map(input: DeleteSymptomFromScheduleResponse): Boolean {
        return input.response != null
    }
}

class MappingDeleteScheduleRemoteAsUIModel :
    Mapper<Any, Boolean> {
    override fun map(input: Any): Boolean {
        return input != null
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
                startDate = convertDateTimeToMillis(startDate),
                reminderTime = getReminderTimeFromDateTime(startDate.toString()),
                reminderBefore = ReminderBefore.createReminderBefore(
                    entity?.notifyBeforeMinutes ?: 0
                ),
                notes = entity?.notes ?: "",
                symptom = scheduleSymptoms?.firstOrNull()?.symptom?.let { symptomResponse ->
                    MappingSymptomRemoteAsModel().map(symptomResponse)
                },
            )
        }
    } ?: listOf()
}


class MappingMedicalReminderAsMedicalReminderTrack(val context: Context): Mapper<MedicalReminder, MedicalReminderTrack> {
    override fun map(input: MedicalReminder): MedicalReminderTrack = input.run {
        MedicalReminderTrack(
            medicalReminderId = id,
            doctorName = doctorName,
            location = location,
            phoneNumber = phoneNumber,
            startDate = startDate,
            reminderTime = reminderTime,
            reminderBefore = reminderBefore,
            notes = notes,
            symptom = symptom,
            oldSymptomId = symptom?.id,
            editable = true,
        )
    }
}
