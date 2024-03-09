package com.cancer.yaqeen.presentation.ui.main.treatment.add.routine_test

import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Day
import com.cancer.yaqeen.data.features.home.schedule.medication.models.MedicationTrack
import com.cancer.yaqeen.data.features.home.schedule.medication.models.MedicationType
import com.cancer.yaqeen.data.features.home.schedule.medication.models.PeriodTimeEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Time
import com.cancer.yaqeen.data.features.home.schedule.medication.models.UnitType
import com.cancer.yaqeen.data.features.home.schedule.medication.requests.AddMedicationRequestBuilder
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.RoutineTestTrack
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.presentation.util.SingleLiveEvent
import com.cancer.yaqeen.presentation.util.generateFileName
import com.cancer.yaqeen.presentation.util.getCurrentTimeMillis
import com.cancer.yaqeen.presentation.util.timestampToDay
import com.cancer.yaqeen.presentation.util.timestampToMonth
import com.cancer.yaqeen.presentation.util.timestampToYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class RoutineTestViewModel @Inject constructor(
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
) : ViewModel() {

    private var viewModelJob: Job? = null

    private val _viewStateAddRoutineTest = SingleLiveEvent<Boolean?>()
    val viewStateAddRoutineTest: LiveData<Boolean?> = _viewStateAddRoutineTest

    private val _viewStateEditRoutineTest = SingleLiveEvent<Boolean?>()
    val viewStateEditRoutineTest: LiveData<Boolean?> = _viewStateEditRoutineTest

    private val _viewStateLoading = MutableStateFlow<Boolean>(false)
    val viewStateLoading = _viewStateLoading.asStateFlow()

    private val _viewStateError = MutableStateFlow<ErrorEntity?>(null)
    val viewStateError = _viewStateError.asStateFlow()

    private var routineTestTrackField: ObservableField<RoutineTestTrack> = ObservableField(
        RoutineTestTrack()
    )

    fun createPhoto(uri: Uri): Photo {
        val timeMillis = getCurrentTimeMillis()
        val fileName = generateFileName()
        return Photo(
            id = timeMillis,
            uri = uri,
            imageName = fileName
        )
    }

    fun deleteRoutinePhoto() =
        routineTestTrackField.get()?.also {
            it.photo = null
        }

    fun addRoutinePhoto(photo: Photo) =
        routineTestTrackField.get()?.also {
            it.photo = photo
        }

    fun getRoutinePhoto():Photo? =
        routineTestTrackField.get()?.photo

    fun setRoutineTestInfo(routineTestName: String, periodTime: Time, specificDays: List<Day>) =
        routineTestTrackField.get()?.also {
            it.routineTestName = routineTestName
            it.periodTime = periodTime
            it.specificDays = specificDays
        }

    fun selectSpecificDays(specificDays: List<Day>) =
        routineTestTrackField.get()?.also {
            it.specificDays = specificDays
        }

    fun selectStartDate(startDate: Long?) =
        routineTestTrackField.get()?.also {
            it.startDate = startDate
        }

    fun selectReminderTime(reminderTime: ReminderTime?) =
        routineTestTrackField.get()?.also {
            it.reminderTime = reminderTime
        }

    fun selectNotesAndReminderBeforeTime(notes: String, reminderBeforeTime: String) =
        routineTestTrackField.get()?.also {
            it.notes = notes
            it.reminderBeforeTime = reminderBeforeTime
        }

    fun getRoutineTestTrack(): RoutineTestTrack? =
        routineTestTrackField.get()

    fun resetRoutineTestTrack() {
        routineTestTrackField.set(RoutineTestTrack())
    }


    private fun createCronExpression(
        minutes: String,
        startingHour: String,
        time: Time?,
        startingDate: Long?,
        specificDays: List<Day>?
    ): String {
        val seconds = "0"
        val hours = "$startingHour${time?.cronExpression ?: ""}"
        val startingDay = startingDate?.timestampToDay() ?: "0"
        val startingMonth = startingDate?.timestampToMonth() ?: "0"
        val startingYear = startingDate?.timestampToYear() ?: "0"
        val (dayOfMonth, dayOfWeek) = when (time?.id) {
            PeriodTimeEnum.DAY_AFTER_DAY.id -> "$startingDay/2" to "*"
            PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id -> "*" to (specificDays?.map { it.id }?.joinToString(separator = ",") { it.toString() } ?: "")
            else -> "$startingDay/1" to "*"
        }
        val month = "$startingMonth/1"
        val year = "$startingYear/1"

        return "$minutes $hours $dayOfMonth $month $dayOfWeek"
    }

    fun setRoutineTestTrack(routineTestTrack: RoutineTestTrack) {
        routineTestTrackField.set(routineTestTrack)
    }

    fun userIsLoggedIn() =
        prefEncryptionUtil.isLogged

    private suspend fun emitError(errorEntity: ErrorEntity?) {
        _viewStateError.emit(errorEntity)
        _viewStateError.emit(null)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob = null
    }
}