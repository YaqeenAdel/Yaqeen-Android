package com.cancer.yaqeen.data.features.home.schedule.data_sources

import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.base.BaseDataSource
import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import com.cancer.yaqeen.data.room.YaqeenDao
import javax.inject.Inject

class ScheduleLocalDataSourceImpl @Inject constructor(
    private val yaqeenDao: YaqeenDao,
    errorHandler: ErrorHandlerImpl,
    private val prefEncryptionUtil: SharedPrefEncryptionUtil
): BaseDataSource(errorHandler), IScheduleLocalDataSource{

    override suspend fun saveMedication(medication: MedicationDB): DataState<Long> =
        getResultDao { yaqeenDao.insertMedication(medication) }

    override suspend fun saveRoutineTest(routineTest: RoutineTestDB): DataState<Long> =
        getResultDao { yaqeenDao.insertRoutineTest(routineTest) }

    override suspend fun saveMedicalAppointment(medicalAppointment: MedicalAppointmentDB): DataState<Long> =
        getResultDao { yaqeenDao.insertMedicalAppointment(medicalAppointment) }
}