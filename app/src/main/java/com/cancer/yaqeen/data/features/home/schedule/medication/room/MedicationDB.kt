package com.cancer.yaqeen.data.features.home.schedule.medication.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "Medication")
data class MedicationDB(
    @PrimaryKey
    val medicationId: Int = 0,
    val medicationName: String,
    val medicationType: String,
    val strengthAmount: String,
    val unitType: String,
    val dosageAmount: String,
    val notes: String,
    val scheduleType: String,
    val cronExpression: String? = null,
    var startDateTime: Long,
//    val startDate: Long,
//    val hour24: Int,
//    val minute: Int,
//    val isAM: Boolean,
//    var time: String,
    val periodTimeId: Int?,
    val specificDaysIds: List<Int> = listOf(),
    var workID: String? = null,
    var workSpecificDaysIDs: List<String> = listOf(),
    var json: String? = null,
    var isReminded: Boolean = false
): Parcelable {
    fun createNotificationMessage(): String{
        return "Hello! Just a friendly reminder to take $strengthAmount: $unitType from $medicationType: '$medicationName' now as prescribed today. Your health is important, so let's stay on track together. \uD83D\uDE0A"
    }
}
