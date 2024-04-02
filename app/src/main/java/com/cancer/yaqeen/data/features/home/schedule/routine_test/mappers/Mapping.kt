package com.cancer.yaqeen.data.features.home.schedule.routine_test.mappers

import android.content.Context
import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.models.MedicationTrack
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.ReminderBefore.Companion.createReminderBefore
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.RoutineTest
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.RoutineTestTrack
import com.cancer.yaqeen.data.features.home.schedule.routine_test.responses.AddRoutineTestResponse
import com.cancer.yaqeen.data.features.home.schedule.routine_test.responses.EditRoutineTestResponse
import com.cancer.yaqeen.data.features.home.schedule.routine_test.responses.GetRoutineTestsResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.ModifyScheduleResponse
import com.cancer.yaqeen.data.utils.createPhotosList
import com.cancer.yaqeen.presentation.ui.main.treatment.getMedicationType
import com.cancer.yaqeen.presentation.ui.main.treatment.getPeriodTimeFromCronExpression
import com.cancer.yaqeen.presentation.ui.main.treatment.getReminderTimeFromCronExpression
import com.cancer.yaqeen.presentation.ui.main.treatment.getSpecificDaysFromCronExpression
import com.cancer.yaqeen.presentation.ui.main.treatment.getStartingDateFromCronExpression
import com.cancer.yaqeen.presentation.ui.main.treatment.getUnitType


class MappingAddRoutineTestWithUploadRemoteAsUIModel :
    Mapper<AddRoutineTestResponse, ModifyScheduleResponse?> {
    override fun map(input: AddRoutineTestResponse): ModifyScheduleResponse? {
        return ModifyScheduleResponse(
            photoIsUploaded = true,
            scheduleIsModified = input.response != null
        )
    }
}
class MappingAddRoutineTestRemoteAsUIModel :
    Mapper<AddRoutineTestResponse, Boolean> {
    override fun map(input: AddRoutineTestResponse): Boolean {
        return input.response != null
    }
}

class MappingRoutineTestsRemoteAsModel: Mapper<GetRoutineTestsResponse, List<RoutineTest>> {
    override fun map(input: GetRoutineTestsResponse): List<RoutineTest> = input.schedules?.map {
        it.run {
            RoutineTest(
                id = scheduleID ?: 0,
                routineTestName = entity?.name ?: "",
                reminderBefore = createReminderBefore(entity?.notifyBeforeMinutes ?: 0),
                notes = entity?.notes ?: "",
                photo = createPhotosList(downloadPhotoLinks?.urls?.firstOrNull()?.path, "", downloadPhotoLinks?.urls?.map { it.url }).firstOrNull(),
                scheduleType = entityType ?: "",
                cronExpression = cronExpression ?: "",
                reminderTime = cronExpression?.let { getReminderTimeFromCronExpression(cronExpression) },
                startDate = cronExpression?.let { getStartingDateFromCronExpression(cronExpression) } ?: 0L,
            )
        }
    } ?: listOf()
}

class MappingEditRoutineTestRemoteAsUIModel :
    Mapper<EditRoutineTestResponse, Boolean> {
    override fun map(input: EditRoutineTestResponse): Boolean {
        return input.response != null
    }
}

class MappingEditRoutineTestWithUploadRemoteAsUIModel :
    Mapper<EditRoutineTestResponse, ModifyScheduleResponse?> {
    override fun map(input: EditRoutineTestResponse): ModifyScheduleResponse? {
        return ModifyScheduleResponse(
            photoIsUploaded = true,
            scheduleIsModified = input.response != null
        )
    }
}


class MappingRoutineTestAsRoutineTestTrack(val context: Context): Mapper<RoutineTest, RoutineTestTrack> {
    override fun map(input: RoutineTest): RoutineTestTrack = input.run {
        RoutineTestTrack(
            routineTestName = routineTestName,
            photo = photo,
            periodTime = getPeriodTimeFromCronExpression(cronExpression),
            specificDays = getSpecificDaysFromCronExpression(cronExpression),
            startDate = getStartingDateFromCronExpression(cronExpression),
            reminderTime = getReminderTimeFromCronExpression(cronExpression),
            reminderBefore = reminderBefore,
            notes = notes,
            editable = true,
            routineTestId = id
        )
    }
}