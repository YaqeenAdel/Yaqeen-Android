package com.cancer.yaqeen.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import android.os.Build.VERSION_CODES.Q
import android.os.PersistableBundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkInfo
import androidx.work.WorkerParameters
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.PeriodTimeEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.medication.room.ReminderStatus
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.room.YaqeenDao
import com.cancer.yaqeen.data.utils.fromJson
import com.cancer.yaqeen.data.utils.toJson
import com.cancer.yaqeen.domain.features.home.schedule.medication.EditLocalMedicationUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.GetLocalMedicationsUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.GetLocalRemindedMedicationsUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.EditLocalRoutineTestUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.GetLocalRemindedRoutineTestsUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.GetLocalRoutineTestsUseCase
import com.cancer.yaqeen.presentation.receiver.NotificationReceiver
import com.cancer.yaqeen.presentation.ui.MainActivity
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.Constants.ACTION_KEY
import com.cancer.yaqeen.presentation.util.Constants.OBJECT_JSON
import com.cancer.yaqeen.presentation.util.Constants.UPDATE_LOCAL_MEDICATION_ACTION
import com.cancer.yaqeen.presentation.util.Constants.UPDATE_LOCAL_ROUTINE_TEST_ACTION
import com.cancer.yaqeen.presentation.util.Constants.UPDATE_LOCAL_REMINDED_SCHEDULES_ACTION
import com.cancer.yaqeen.presentation.util.Constants.UPDATE_LOCAL_SCHEDULES_ACTION
import com.cancer.yaqeen.presentation.util.scheduleJobServicePeriodically
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Random
import java.util.concurrent.TimeUnit
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
    val getLocalRemindedMedicationsUseCase: GetLocalRemindedMedicationsUseCase,
    val getLocalRemindedRoutineTestsUseCase: GetLocalRemindedRoutineTestsUseCase,
) : CoroutineWorker(context, params) {

    @Inject
    lateinit var yaqeenDao: YaqeenDao

    private val workerReminder: ReminderManager by lazy {
        AlarmReminder(context)
    }

    private val workerReminderPeriodically: ReminderManager by lazy {
        WorkerReminder(context)
    }

    override suspend fun doWork(): Result {
        try {
            val objectJsonValue = inputData.getString(OBJECT_JSON)
            Log.d("ReminderWorker", "Worker doWork: $objectJsonValue")
            Log.d("ReminderWorker", "Worker doWork: ${inputData.getString(ACTION_KEY)}")

            when (val actionName = inputData.getString(ACTION_KEY)) {
                UPDATE_LOCAL_SCHEDULES_ACTION -> {
                    getSchedules()
                }

                UPDATE_LOCAL_REMINDED_SCHEDULES_ACTION -> {
//                    prefEncryptionUtil.workRunningInMillis =
//                        System.currentTimeMillis() + PeriodTimeEnum.EVERY_3_HOURS.timeInMillis
                    getRemindedSchedules()
                }

                UPDATE_LOCAL_MEDICATION_ACTION -> {
                    val medication: MedicationDB? =
                        objectJsonValue.toString().fromJson(MedicationDB::class.java)
                    medication?.let {
                        setMedicationReminder(
                            it.apply {
//                                isReminded = true
                                statusReminder = ReminderStatus.REMINDED
                                workID = null
                                workSpecificDaysIDs = listOf()
                                startDateTime = increaseDateTime(it.startDateTime, it.periodTimeId)
                            }
                        )
//                        updateMedication(
//                            it.apply {
//                                workID = null
//                                workSpecificDaysIDs = listOf()
//                                isReminded = true
//                                statusReminder = ReminderStatus.NEW
//                                startDateTime = increaseDateTime(it.startDateTime, it.periodTimeId)
//                            }
//                        )
                    }
                }

                UPDATE_LOCAL_ROUTINE_TEST_ACTION -> {
                    val routineTest: RoutineTestDB? =
                        objectJsonValue.toString().fromJson(RoutineTestDB::class.java)
                    routineTest?.let {
                        setRoutineTestReminder(
                            it.apply {
//                                isReminded = true
                                statusReminder = ReminderStatus.REMINDED
                                workID = null
                                workSpecificDaysIDs = listOf()
                                startDateTime = increaseDateTime(it.startDateTime, it.periodTimeId)
                            }
                        )
//                        updateRoutineTest(
//                            it.apply {
//                                workID = null
//                                workSpecificDaysIDs = listOf()
//                                isReminded = true
//                                statusReminder = ReminderStatus.NEW
//                                startDateTime = increaseDateTime(it.startDateTime, it.periodTimeId)
//                            }
//                        )
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
        } catch (e: Exception) {

            scheduleJobServicePeriodically(context = context, bundle = PersistableBundle().apply {
                putString(
                    ACTION_KEY,
                    UPDATE_LOCAL_REMINDED_SCHEDULES_ACTION
                )
            })
            Log.d("ReminderWorker", "Worker Exception: $e")
            if (SDK_INT >= Build.VERSION_CODES.S) {
//                throw Exception("Reminder-Worker: $stopReason $e")
            } else {
//                throw Exception("Reminder-Worker: ${WorkInfo.STOP_REASON_UNKNOWN} $e")
            }
            return Result.retry()
        } finally {
            Log.d("ReminderWorker", "Worker doWork: finally")

//            throw Exception("Reminder-Worker: $stopReason")
        }
    }

    private fun getSchedules() {
        getLocalMedications()
        getLocalRoutineTests()
    }

    private fun getLocalMedications() {
        GlobalScope.launch(Dispatchers.Default) {
            getLocalMedicationsUseCase()
                .catch {
                    Log.d("ReminderWorker", "getLocalMedications, catch: $this")
                }.collectLatest {
                    it.data?.let { localMedications ->
                        if (localMedications.isNotEmpty())
                            setMedicationReminders(localMedications)
                    }
                }
        }
    }

    private fun getLocalRoutineTests() {
        GlobalScope.launch(Dispatchers.Default) {
            getLocalRoutineTestsUseCase()
                .catch {

                }.collectLatest {
                    it.data?.let { localRoutineTests ->
                        if (localRoutineTests.isNotEmpty())
                            setRoutineTestReminders(localRoutineTests)
                    }
                }
        }
    }

    private fun getRemindedSchedules() {
        getLocalRemindedMedications()
        getLocalRemindedRoutineTests()
    }

    private fun getLocalRemindedMedications() {
        Log.d("ReminderWorker", "Worker getLocalRemindedMedications")
        GlobalScope.launch(Dispatchers.IO) {
            getLocalRemindedMedicationsUseCase()
                .catch {

                }.collectLatest {
                    it.data?.let { localMedications ->
                        Log.d("ReminderWorker", "Worker localMedications $localMedications")
                        if (localMedications.isNotEmpty())
                            setMedicationReminders(localMedications)
                    }
                }
        }
    }

    private fun getLocalRemindedRoutineTests() {
        GlobalScope.launch(Dispatchers.IO) {
            getLocalRemindedRoutineTestsUseCase()
                .catch {

                }.collectLatest {
                    it.data?.let { localRoutineTests ->
                        if (localRoutineTests.isNotEmpty())
                            setRoutineTestReminders(localRoutineTests)
                    }
                }
        }
    }

    private fun setMedicationReminders(medications: List<MedicationDB>) {
        Log.d("ReminderWorker", "Worker setMedicationReminders")
        medications.onEach { medication ->
            setMedicationReminder(medication)
        }
    }

    private fun setMedicationReminder(medication: MedicationDB) {
        if (medication.periodTimeId == PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id) {
            var uuids = listOf<String>()
            try {
                uuids = workerReminder.setReminderDays(medication.apply { json = toJson() })
                medication.statusReminder = ReminderStatus.NEW
            } catch (_: Exception) {

            } finally {
                updateMedication(
                    medication.apply {
                        workSpecificDaysIDs = uuids
                    }
                )
            }
        } else {
            var uuid = ""

            try {
                uuid = workerReminder.setReminder(medication.apply { json = toJson() })
                medication.statusReminder = ReminderStatus.NEW
            } catch (_: Exception) {

            } finally {
                Log.d("ReminderWorker", "Worker setMedicationReminder uuid: $uuid")
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
            ).catch {}.collect()
        }
    }

    private fun setRoutineTestReminders(routineTests: List<RoutineTestDB>) {
        routineTests.onEach { routineTest ->
            setRoutineTestReminder(routineTest)
        }
    }

    private fun setRoutineTestReminder(routineTest: RoutineTestDB) {
        if (routineTest.periodTimeId == PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id) {
            var uuids = listOf<String>()
            var workBeforeID: String? = null
            try {
                val reminderInfo = workerReminder.setReminderDays(routineTest.apply {
                    json = toJson()
                })
                uuids = reminderInfo.first
                workBeforeID = reminderInfo.second
                routineTest.statusReminder = ReminderStatus.NEW
            } catch (_: Exception) {

            } finally {
                updateRoutineTest(
                    routineTest.apply {
                        this.workSpecificDaysIDs = uuids
                        this.workBeforeID = workBeforeID
                    }
                )
            }
        } else {
            var periodicWorkID = ""
            var workBeforeID: String? = null
            try {
                val reminderInfo = workerReminder.setReminder(routineTest.apply {
                    json = toJson()
                })
                periodicWorkID = reminderInfo.first
                workBeforeID = reminderInfo.second
                routineTest.statusReminder = ReminderStatus.NEW

            } catch (_: Exception) {

            } finally {
                updateRoutineTest(
                    routineTest.apply {
                        this.workID = periodicWorkID
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
            ).catch {}.collect()
        }
    }

    private fun increaseDateTime(startDateTime: Long, periodTimeId: Int?): Long =
        when (periodTimeId) {
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

            PeriodTimeEnum.EVERY_WEEK.id, PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id -> {
                startDateTime + PeriodTimeEnum.EVERY_WEEK.timeInMillis
            }

            PeriodTimeEnum.EVERY_MONTH.id -> {
                startDateTime + PeriodTimeEnum.EVERY_MONTH.timeInMillis
            }

            else -> {
                0L
            }
        }

    private fun createForegroundInfo(): ForegroundInfo {
        return if (SDK_INT >= Q) {
            ForegroundInfo(
                Random().nextInt(),
                sendNotification(),
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            ForegroundInfo(Random().nextInt(), sendNotification())
        }
    }

    private fun sendNotification(): Notification {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
        val notificationManager =
            applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val pendingIntent = getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(applicationContext, "NOTIFICATION_CHANNEL")
            .setSmallIcon(R.drawable.logo_launcher)
            .setContentTitle("Android 14 service test")
            .setContentText("SDK14")
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        if (SDK_INT >= O) {
            notification.setChannelId("NOTIFICATION_CHANNEL")
            val channel =
                NotificationChannel("NOTIFICATION_CHANNEL", "NOTIFICATION_NAME", IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(101, notification.build())
        return notification.build()
    }

}