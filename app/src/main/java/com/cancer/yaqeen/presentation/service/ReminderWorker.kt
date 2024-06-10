package com.cancer.yaqeen.presentation.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.cancer.yaqeen.data.features.home.schedule.medication.models.PeriodTimeEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.room.YaqeenDao
import com.cancer.yaqeen.data.utils.fromJson
import com.cancer.yaqeen.data.utils.toJson
import com.cancer.yaqeen.domain.features.home.schedule.medication.EditLocalMedicationUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.GetLocalMedicationsUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.EditLocalRoutineTestUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.GetLocalRoutineTestsUseCase
import com.cancer.yaqeen.presentation.receiver.NotificationReceiver
import com.cancer.yaqeen.presentation.util.Constants.ACTION_KEY
import com.cancer.yaqeen.presentation.util.Constants.OBJECT_JSON
import com.cancer.yaqeen.presentation.util.Constants.UPDATE_LOCAL_MEDICATION_ACTION
import com.cancer.yaqeen.presentation.util.Constants.UPDATE_LOCAL_ROUTINE_TEST_ACTION
import com.cancer.yaqeen.presentation.util.Constants.UPDATE_LOCAL_SCHEDULES_ACTION
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters,
    val prefEncryptionUtil: SharedPrefEncryptionUtil,
    val editLocalMedicationUseCase: EditLocalMedicationUseCase,
    val editLocalRoutineTestUseCase: EditLocalRoutineTestUseCase,
    val getLocalMedicationsUseCase: GetLocalMedicationsUseCase,
    val getLocalRoutineTestsUseCase: GetLocalRoutineTestsUseCase,
) : Worker(context, params) {

    @Inject
    lateinit var yaqeenDao: YaqeenDao

    private val workerReminder: ReminderManager by lazy {
        AlarmReminder(context)
    }

    private val workerReminderPeriodically: ReminderManager by lazy {
        WorkerReminder(context)
    }

    override fun doWork(): Result {
        val objectJsonValue = inputData.getString(OBJECT_JSON)

        when (val actionName = inputData.getString(ACTION_KEY)) {
            UPDATE_LOCAL_SCHEDULES_ACTION -> {
                prefEncryptionUtil.workRunningInMillis =
                    System.currentTimeMillis() + PeriodTimeEnum.EVERY_3_HOURS.timeInMillis
                getSchedules()
            }
            UPDATE_LOCAL_MEDICATION_ACTION -> {
                val medication: MedicationDB? =
                    objectJsonValue.toString().fromJson(MedicationDB::class.java)
                medication?.let {
                    updateMedication(
                        it.apply {
                            workID = null
                            workSpecificDaysIDs = listOf()
                            isReminded = true
                            startDateTime = increaseDateTime(it.startDateTime, it.periodTimeId)
                        }
                    )
                }
            }
            UPDATE_LOCAL_ROUTINE_TEST_ACTION -> {
                val routineTest: RoutineTestDB? =
                    objectJsonValue.toString().fromJson(RoutineTestDB::class.java)
                routineTest?.let {

                    updateRoutineTest(
                        it.apply {
                            workID = null
                            workSpecificDaysIDs = listOf()
                            isReminded = true
                            startDateTime = increaseDateTime(it.startDateTime, it.periodTimeId)
                        }
                    )
                }

            }
            else -> {
                objectJsonValue?.let {
                    val intent = Intent(context, NotificationReceiver::class.java).apply {
                        action = actionName
                        data = Uri.parse(objectJsonValue)
                    }

                    applicationContext.sendBroadcast(intent)
                }
            }
        }

        return Result.success()
    }

    private fun getSchedules() {
        getLocalMedications()
        getLocalRoutineTests()
    }

    private fun getLocalMedications() {
        GlobalScope.launch(Dispatchers.Default) {
            getLocalMedicationsUseCase().collectLatest {
                it.data?.let { localMedications ->
                    if (localMedications.isNotEmpty())
                        setMedicationReminders(localMedications)
                }
            }
        }
    }

    private fun getLocalRoutineTests() {
        GlobalScope.launch(Dispatchers.Default) {
            getLocalRoutineTestsUseCase().collectLatest {
                it.data?.let { localRoutineTests ->
                    if (localRoutineTests.isNotEmpty())
                        setRoutineTestReminders(localRoutineTests)
                }
            }
        }
    }

    private fun setMedicationReminders(medications: List<MedicationDB>) {
        medications.onEach { medication ->
            medication.isReminded = false
            if (medication.periodTimeId == PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id) {
                val uuids =
                    workerReminder.setReminderDays(medication.apply { json = toJson() })

                updateMedication(
                    medication.apply {
                        workSpecificDaysIDs = uuids
                    }
                )

            } else {
                val uuid = workerReminder.setReminder(medication.apply { json = toJson() })

                updateMedication(
                    medication.apply {
                        workID = uuid
                    }
                )
            }
        }
    }

    private fun updateMedication(medication: MedicationDB) {
        GlobalScope.launch((Dispatchers.IO)) {
            editLocalMedicationUseCase(
                medication
            ).collect()
        }
    }

    private fun setRoutineTestReminders(routineTests: List<RoutineTestDB>) {
        routineTests.onEach { routineTest ->
            routineTest.isReminded = false
            if (routineTest.periodTimeId == PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id) {
                val (uuids, workBeforeID) = workerReminder.setReminderDays(routineTest.apply {
                    json = toJson()
                })

                updateRoutineTest(
                    routineTest.apply {
                        workSpecificDaysIDs = uuids
                        this.workBeforeID = workBeforeID
                    }
                )
            } else {
                val (periodicWorkID, workBeforeID) = workerReminder.setReminder(routineTest.apply {
                    json = toJson()
                })

                updateRoutineTest(
                    routineTest.apply {
                        workID = periodicWorkID
                        this.workBeforeID = workBeforeID
                    }
                )

            }
        }
    }

    private fun updateRoutineTest(routineTest: RoutineTestDB) {
        GlobalScope.launch((Dispatchers.IO)) {
            editLocalRoutineTestUseCase(
                routineTest
            ).collect()
        }
    }

    private fun increaseDateTime(startDateTime: Long, periodTimeId: Int?): Long =
        when(periodTimeId){
            PeriodTimeEnum.EVERY_DAY.id -> {
                startDateTime + PeriodTimeEnum.EVERY_DAY.timeInMillis
            }
            PeriodTimeEnum.EVERY_8_HOURS.id -> {
                startDateTime + PeriodTimeEnum.EVERY_8_HOURS.timeInMillis
            }
            PeriodTimeEnum.EVERY_12_HOURS.id -> {
                startDateTime + PeriodTimeEnum.EVERY_12_HOURS.timeInMillis
            }
            PeriodTimeEnum.DAY_AFTER_DAY.id -> {
                startDateTime + PeriodTimeEnum.DAY_AFTER_DAY.timeInMillis
            }
            PeriodTimeEnum.EVERY_WEEK.id -> {
                startDateTime + PeriodTimeEnum.EVERY_WEEK.timeInMillis
            }
            PeriodTimeEnum.EVERY_MONTH.id -> {
                startDateTime + PeriodTimeEnum.EVERY_MONTH.timeInMillis
            }
            else -> {
                0L
            }
        }


}