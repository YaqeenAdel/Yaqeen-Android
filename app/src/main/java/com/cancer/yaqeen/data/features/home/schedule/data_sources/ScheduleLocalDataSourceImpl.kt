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

    override suspend fun getMedication(medicationId: Int): DataState<MedicationDB?> =
        getResultDao { yaqeenDao.selectMedication(medicationId) }

    override suspend fun saveMedication(medication: MedicationDB): DataState<Long> =
        getResultDao { yaqeenDao.insertMedication(medication) }

    override suspend fun editMedication(medication: MedicationDB): DataState<Int> =
        getResultDao { yaqeenDao.updateMedication(medication) }

    override suspend fun removeMedication(medicationId: Int): DataState<Int> =
        getResultDao { yaqeenDao.deleteMedication(medicationId) }

    override suspend fun removeMedications(): DataState<Int> =
        getResultDao { yaqeenDao.deleteMedications() }

    override suspend fun getRoutineTest(routineTestId: Int): DataState<RoutineTestDB?> =
        getResultDao { yaqeenDao.selectRoutineTest(routineTestId) }

    override suspend fun saveRoutineTest(routineTest: RoutineTestDB): DataState<Long> =
        getResultDao { yaqeenDao.insertRoutineTest(routineTest) }

    override suspend fun editRoutineTest(routineTest: RoutineTestDB): DataState<Int> =
        getResultDao { yaqeenDao.updateRoutineTest(routineTest) }

    override suspend fun removeRoutineTest(routineTestId: Int): DataState<Int> =
        getResultDao { yaqeenDao.deleteRoutineTest(routineTestId) }

    override suspend fun removeRoutineTests(): DataState<Int> =
        getResultDao { yaqeenDao.deleteRoutineTests() }

    override suspend fun getMedicalAppointment(medicalAppointmentId: Int): DataState<MedicalAppointmentDB?> =
        getResultDao { yaqeenDao.selectMedicalAppointment(medicalAppointmentId) }

    override suspend fun saveMedicalAppointment(medicalAppointment: MedicalAppointmentDB): DataState<Long> =
        getResultDao { yaqeenDao.insertMedicalAppointment(medicalAppointment) }

    override suspend fun editMedicalAppointment(medicalAppointment: MedicalAppointmentDB): DataState<Int> =
        getResultDao { yaqeenDao.updateMedicalAppointment(medicalAppointment) }

    override suspend fun removeMedicalAppointment(medicalAppointmentId: Int): DataState<Int> =
        getResultDao { yaqeenDao.deleteMedicalAppointment(medicalAppointmentId) }

    override suspend fun removeMedicalAppointments(): DataState<Int> =
        getResultDao { yaqeenDao.deleteMedicalAppointments() }
}