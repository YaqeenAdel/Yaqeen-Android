package com.cancer.yaqeen.data.features.home.schedule

import com.cancer.yaqeen.data.features.home.schedule.medication.mappers.MappingAddMedicationRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.medication.mappers.MappingMedicationsRemindersRemoteAsModel
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.requests.AddMedicationRequest
import com.cancer.yaqeen.data.network.base.BaseDataSource
import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.apis.YaqeenAPI
import com.cancer.yaqeen.data.network.base.flowStatus
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val apiService: YaqeenAPI,
    errorHandler: ErrorHandlerImpl,
    private val prefEncryptionUtil: SharedPrefEncryptionUtil
): BaseDataSource(errorHandler), IScheduleRepository {


    override suspend fun addMedication(request: AddMedicationRequest): Flow<DataState<Boolean>> =
        flowStatus {
            getResultRestAPI(MappingAddMedicationRemoteAsUIModel()){
                apiService.addMedication(request)
            }
        }

    override suspend fun getMedicationsReminders(scheduleType: String): Flow<DataState<List<Medication>>> =
        flowStatus {
            getResultRestAPI(MappingMedicationsRemindersRemoteAsModel()){
                apiService.getMedicationsReminders(scheduleType)
            }
        }


}