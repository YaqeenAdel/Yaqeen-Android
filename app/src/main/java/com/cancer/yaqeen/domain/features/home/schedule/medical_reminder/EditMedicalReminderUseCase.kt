package com.cancer.yaqeen.domain.features.home.schedule.medical_reminder

import com.cancer.yaqeen.data.features.home.schedule.IScheduleRepository
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models.ModifyMedicalReminder
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.requests.AddMedicalReminderRequest
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EditMedicalReminderUseCase @Inject constructor(private val repository: IScheduleRepository) {
    suspend operator fun invoke(
        scheduleId: Int,
        request: AddMedicalReminderRequest,
        symptomId: Int?
    ): Flow<DataState<ModifyMedicalReminder?>> =
        repository.editMedicalReminder(
            scheduleId,
            request,
            symptomId
        )
}