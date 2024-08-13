package com.cancer.yaqeen.data.features.home.schedule.medication.mappers

import android.content.Context
import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.models.MedicationTrack
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Schedule
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ScheduleAppointment
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ScheduleMedication
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ScheduleRoutineTest
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ScheduleType
import com.cancer.yaqeen.data.features.home.schedule.medication.responses.AddMedicationResponse
import com.cancer.yaqeen.data.features.home.schedule.medication.responses.EditMedicationResponse
import com.cancer.yaqeen.data.features.home.schedule.medication.responses.SchedulesResponse
import com.cancer.yaqeen.data.features.home.schedule.medication.responses.TodayScheduleResponse
import com.cancer.yaqeen.data.features.home.schedule.medication.responses.TodaySchedulesResponse
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.ReminderBefore
import com.cancer.yaqeen.data.utils.formatDateTimeAPIToTimeUI
import com.cancer.yaqeen.data.utils.isCurrentTodayAndAfterTimeNow
import com.cancer.yaqeen.presentation.ui.main.treatment.getMedicationType
import com.cancer.yaqeen.presentation.ui.main.treatment.getPeriodTimeFromCronExpression
import com.cancer.yaqeen.presentation.ui.main.treatment.getReminderTimeFromCronExpression
import com.cancer.yaqeen.presentation.ui.main.treatment.getSpecificDaysFromCronExpression
import com.cancer.yaqeen.presentation.ui.main.treatment.getStartingDateFromCronExpression
import com.cancer.yaqeen.presentation.ui.main.treatment.getUnitType


class MappingAddMedicationRemoteAsUIModel :
    Mapper<AddMedicationResponse, Int?> {
    override fun map(input: AddMedicationResponse): Int? {
        return input.response?.scheduleID
    }
}


class MappingMedicationsRemindersRemoteAsModel : Mapper<SchedulesResponse, List<Medication>> {
    override fun map(input: SchedulesResponse): List<Medication> = input.schedules?.map {
        it.run {
            Medication(
                id = scheduleID ?: 0,
                medicationName = entity?.name ?: "",
                medicationType = entity?.type ?: "",
                strengthAmount = entity?.strengthTimes ?: 0,
                unitType = entity?.unit ?: "",
                dosageAmount = entity?.dosageTimes ?: 0,
                notes = entity?.notes ?: "",
                scheduleType = entityType ?: "",
                cronExpression = cronExpression ?: "",
                reminderTime = cronExpression?.let {
                    getReminderTimeFromCronExpression(
                        cronExpression
                    )
                }
            )
        }
    } ?: listOf()
}

class MappingMedicationAsMedicationTrack(val context: Context) :
    Mapper<Medication, MedicationTrack> {
    override fun map(input: Medication): MedicationTrack = input.run {
        MedicationTrack(
            medicationType = getMedicationType(context, medicationType),
            medicationName = medicationName,
            unitType = getUnitType(context, unitType),
            strengthAmount = strengthAmount.toString(),
            dosageAmount = dosageAmount.toString(),
            periodTime = getPeriodTimeFromCronExpression(cronExpression),
            specificDays = getSpecificDaysFromCronExpression(cronExpression),
            startDate = getStartingDateFromCronExpression(cronExpression),
            reminderTime = getReminderTimeFromCronExpression(cronExpression),
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

private fun mapRemindersFromNowRemoteByEvents(response: TodaySchedulesResponse): List<Schedule> {
    val schedules: MutableList<Schedule> = arrayListOf()
    response.schedules?.map { schedule ->
        if (schedule.entityType == ScheduleType.MEDICAL_REMINDER.scheduleType){
            schedules.add(
                MappingReminderTodayRemoteAsScheduleAppointmentModel().map(schedule)
            )
        }else {
            schedule.scheduledEvents?.events?.distinctBy { it.scheduledTime.toString() }
                ?.map { event ->
                    if (isCurrentTodayAndAfterTimeNow(event.scheduledTime))
                        schedules.add(
                            when (schedule.entityType) {
                                ScheduleType.MEDICATION.scheduleType ->
                                    MappingReminderTodayRemoteAsScheduleMedicationModel(event.scheduledTime).map(
                                        schedule
                                    )

                                else -> MappingReminderTodayRemoteAsScheduleRoutineTestModel(event.scheduledTime).map(
                                    schedule
                                )
                            }
                        )
                }
        }
    }

    return schedules
}

class MappingRemindersFromNowRemoteAsModel : Mapper<TodaySchedulesResponse, List<Schedule>> {
    override fun map(input: TodaySchedulesResponse): List<Schedule> {
        return mapRemindersFromNowRemoteByEvents(input)
    }
}

class MappingReminderTodayRemoteAsScheduleMedicationModel(private val scheduledTime: String?) :
    Mapper<TodayScheduleResponse, Schedule> {
    override fun map(input: TodayScheduleResponse): Schedule =
        with(input) {
            Schedule(
                id = scheduleID ?: 0,
                medication = ScheduleMedication(
                    medicationName = entity?.name ?: "",
                    medicationType = entity?.type ?: "",
                    strengthAmount = entity?.strengthTimes ?: 0,
                    unitType = entity?.unit ?: "",
                    dosageAmount = entity?.dosageTimes ?: 0,
                ),
                notes = entity?.notes ?: "",
                scheduleType = entityType ?: "",
                cronExpression = "",
                scheduledTimes = listOf(),
//                scheduledTimes = scheduledEvents?.events?.map { event -> event.scheduledTime.toString() } ?: listOf(),
                scheduledTodayTime = formatDateTimeAPIToTimeUI(scheduledTime)
            )
        }
}

class MappingReminderTodayRemoteAsScheduleAppointmentModel(private val scheduledTime: String? = null) :
    Mapper<TodayScheduleResponse, Schedule> {
    override fun map(input: TodayScheduleResponse): Schedule =
        with(input) {
            Schedule(
                id = scheduleID ?: 0,
                appointment = ScheduleAppointment(
                    doctorName = entity?.physician ?: "",
                    location = entity?.location ?: "",
                    phoneNumber = entity?.phoneNumber ?: "",
                    reminderBefore = ReminderBefore.createReminderBefore(
                        entity?.notifyBeforeMinutes ?: 0
                    )
                ),
                notes = entity?.notes ?: "",
                scheduleType = entityType ?: "",
                cronExpression = cronExpression.toString(),
                reminderTime = getReminderTimeFromCronExpression(cronExpression.toString()),
                scheduledTimes = scheduledEvents?.events?.map { event -> event.scheduledTime.toString() }
                    ?: listOf(),
                scheduledTodayTime = formatDateTimeAPIToTimeUI(scheduledTime)
            )
        }
}

class MappingReminderTodayRemoteAsScheduleRoutineTestModel(private val scheduledTime: String?) :
    Mapper<TodayScheduleResponse, Schedule> {
    override fun map(input: TodayScheduleResponse): Schedule =
        with(input) {
            Schedule(
                id = scheduleID ?: 0,
                routineTest = ScheduleRoutineTest(
                    routineTestName = entity?.name ?: "",
                    reminderBefore = ReminderBefore.createReminderBefore(
                        entity?.notifyBeforeMinutes ?: 0
                    )
                ),
                notes = entity?.notes ?: "",
                scheduleType = entityType ?: "",
                cronExpression = "",
                scheduledTimes = scheduledEvents?.events?.map { event -> event.scheduledTime.toString() }
                    ?: listOf(),
                scheduledTodayTime = formatDateTimeAPIToTimeUI(scheduledTime)
            )
        }
}

class MappingReminderFromNowRemoteAsScheduleMedicationModel :
    Mapper<TodayScheduleResponse, Schedule> {
    override fun map(input: TodayScheduleResponse): Schedule =
        with(input) {
            Schedule(
                id = scheduleID ?: 0,
                medication = ScheduleMedication(
                    medicationName = entity?.name ?: "",
                    medicationType = entity?.type ?: "",
                    strengthAmount = entity?.strengthTimes ?: 0,
                    unitType = entity?.unit ?: "",
                    dosageAmount = entity?.dosageTimes ?: 0,
                ),
                notes = entity?.notes ?: "",
                scheduleType = entityType ?: "",
                cronExpression = "",
                scheduledTimes = scheduledEvents?.events?.map { event -> event.scheduledTime.toString() }
                    ?: listOf(),
                scheduledTodayTime = formatDateTimeAPIToTimeUI(
                    scheduledEvents?.events?.firstOrNull { event ->
                        isCurrentTodayAndAfterTimeNow(event.scheduledTime)
                    }?.scheduledTime
                )
            )
        }
}

class MappingReminderFromNowRemoteAsScheduleAppointmentModel :
    Mapper<TodayScheduleResponse, Schedule> {
    override fun map(input: TodayScheduleResponse): Schedule =
        with(input) {
            Schedule(
                id = scheduleID ?: 0,
                appointment = ScheduleAppointment(
                    doctorName = entity?.physician ?: "",
                    location = entity?.location ?: "",
                    phoneNumber = entity?.phoneNumber ?: "",
                    reminderBefore = ReminderBefore.createReminderBefore(
                        entity?.notifyBeforeMinutes ?: 0
                    )
                ),
                notes = entity?.notes ?: "",
                scheduleType = entityType ?: "",
                cronExpression = "",
                scheduledTimes = scheduledEvents?.events?.map { event -> event.scheduledTime.toString() }
                    ?: listOf(),
                scheduledTodayTime = formatDateTimeAPIToTimeUI(
                    scheduledEvents?.events?.firstOrNull { event ->
                        isCurrentTodayAndAfterTimeNow(event.scheduledTime)
                    }?.scheduledTime
                )
            )
        }
}

class MappingScheduleAsMedicationModel : Mapper<Schedule, Medication> {
    override fun map(input: Schedule): Medication =
        with(input) {
            Medication(
                id = id,
                medicationName = medication?.medicationName ?: "",
                medicationType = medication?.medicationType ?: "",
                strengthAmount = medication?.strengthAmount ?: 0,
                unitType = medication?.unitType ?: "",
                dosageAmount = medication?.dosageAmount ?: 0,
                notes = notes,
                scheduleType = scheduleType,
                cronExpression = cronExpression
            )
        }

}


