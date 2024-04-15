package com.cancer.yaqeen.data.features.home.schedule.medication.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime
import kotlinx.android.parcel.Parcelize
import java.util.UUID

@Parcelize
@Entity(tableName = "Medication")
data class MedicationDB(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val medicationId: Int? = null,
    val medicationName: String,
    val medicationType: String,
    val strengthAmount: String,
    val unitType: String,
    val dosageAmount: String,
    val notes: String,
    val scheduleType: String,
    val cronExpression: String? = null,
    val startDate: Long,
    val hour24: Int,
    val minute: Int,
    val periodTimeId: Int?,
    var workID: UUID? = null
): Parcelable
