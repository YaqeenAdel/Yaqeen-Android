package com.cancer.yaqeen.presentation.ui.main.treatment

import android.content.Context
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Day
import com.cancer.yaqeen.data.features.home.schedule.medication.models.DayEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.models.MedicationType
import com.cancer.yaqeen.data.features.home.schedule.medication.models.MedicationTypeEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.models.PeriodTimeEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Time
import com.cancer.yaqeen.data.features.home.schedule.medication.models.UnitType
import com.cancer.yaqeen.data.features.home.schedule.medication.models.UnitTypeEnum
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun getMedicationTypes(context: Context) =
    listOf(
        MedicationType(
            id = MedicationTypeEnum.CAPSULE.id, nameEn = "Capsule", name = context.getString(R.string.capsule), iconResId = MedicationTypeEnum.CAPSULE.iconId
        ),
        MedicationType(
            id = MedicationTypeEnum.PILLS.id, nameEn = "Pills", name = context.getString(R.string.pills), iconResId = MedicationTypeEnum.PILLS.iconId
        ),
        MedicationType(
            id = MedicationTypeEnum.LIQUID.id, nameEn = "Liquid", name = context.getString(R.string.liquid), iconResId = MedicationTypeEnum.LIQUID.iconId
        ),
        MedicationType(
            id = MedicationTypeEnum.INJECTION.id, nameEn = "Injection", name = context.getString(R.string.injection), iconResId = MedicationTypeEnum.INJECTION.iconId
        )
    )

fun getMedicationType(context: Context, medicationType: String): MedicationType?{
    return when(medicationType){
        context.getString(R.string.capsule_enum) -> MedicationType(
            id = MedicationTypeEnum.CAPSULE.id, nameEn = "Capsule", name = context.getString(R.string.capsule), iconResId = MedicationTypeEnum.CAPSULE.iconId
        )
        context.getString(R.string.pills_enum) -> MedicationType(
            id = MedicationTypeEnum.PILLS.id, nameEn = "Pills", name = context.getString(R.string.pills), iconResId = MedicationTypeEnum.PILLS.iconId
        )
        context.getString(R.string.liquid_enum) -> MedicationType(
            id = MedicationTypeEnum.LIQUID.id, nameEn = "Liquid", name = context.getString(R.string.liquid), iconResId = MedicationTypeEnum.LIQUID.iconId
        )
        context.getString(R.string.injection_enum) -> MedicationType(
            id = MedicationTypeEnum.INJECTION.id, nameEn = "Injection", name = context.getString(R.string.injection), iconResId = MedicationTypeEnum.INJECTION.iconId
        )
        else -> null
    }
}

fun getUnitTypes(context: Context) =
    listOf(
        UnitType(
            id = UnitTypeEnum.MG.id, name = context.getString(R.string.mg)
        ),
        UnitType(
            id = UnitTypeEnum.MCG.id, name = context.getString(R.string.mcg)
        ),
        UnitType(
            id = UnitTypeEnum.ML.id, name = context.getString(R.string.ml)
        ),
        UnitType(
            id = UnitTypeEnum.PERCENTAGE.id, name = context.getString(R.string.percentage)
        )
    )

fun getUnitType(context: Context, unitType: String): UnitType?{
    return when(unitType){
        context.getString(R.string.mg_enum) -> UnitType(
            id = UnitTypeEnum.MG.id, name = context.getString(R.string.mg)
        )
        context.getString(R.string.mcg_enum) -> UnitType(
            id = UnitTypeEnum.MCG.id, name = context.getString(R.string.mcg)
        )
        context.getString(R.string.ml_enum) -> UnitType(
            id = UnitTypeEnum.ML.id, name = context.getString(R.string.ml)
        )
        context.getString(R.string.percentage_enum) -> UnitType(
            id = UnitTypeEnum.PERCENTAGE.id, name = context.getString(R.string.percentage)
        )
        else -> null
    }
}

fun getReminderTimeFromCronExpression(cronExpression: String): ReminderTime {
    val fields = cronExpression.split(" ")

    var minute = fields[0]

    minute = if (minute == "0") "00" else minute

    val hourField = fields[1]

    // if time is every 8 or 12 days
    var hour24 = if (hourField.contains("/")){
        val hourFields = hourField.split("/")
        hourFields[0]
    }else {
        hourField
    }

    val (hour12, isAM) = if (hour24.toInt() == 0){
        "12" to true
    } else if (hour24.toInt() == 12){
        "12" to false
    } else if (hour24.toInt() < 12){
        hour24 to true
    }else {
        (hour24.toInt() - 12).toString() to false
    }

    hour24 = if (hour24 == "0") "00" else hour24

    return ReminderTime(
        hour12, hour24, minute, "", isAM, "$hour12:$minute"
    )
}

fun getPeriodTimeFromCronExpression(cronExpression: String): Time {
    val fields = cronExpression.split(" ")

    val hourField = fields[1]

    // if time is every 8 or 12 hours
    val periodTime: Time = if (hourField.contains("/")){
        val hourFields = hourField.split("/")
        if (hourFields[1] == "8")
            Time(
                id = PeriodTimeEnum.EVERY_8_HOURS.id, timeEn = "", timeAr = "", cronExpression = PeriodTimeEnum.EVERY_8_HOURS.cronExpression
            )
        else
            Time(
                id = PeriodTimeEnum.EVERY_12_HOURS.id, timeEn = "", timeAr = "", cronExpression = PeriodTimeEnum.EVERY_12_HOURS.cronExpression
            )

    }else {
        val dayOfMonthField = fields[2]

        if(dayOfMonthField == "*")
            Time(
                id = PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id, timeEn = "", timeAr = "", cronExpression = PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.cronExpression
            )
        else{

            when(dayOfMonthField.split("/")[1]){
                "1" -> Time(
                    id = PeriodTimeEnum.EVERY_DAY.id, timeEn = "", timeAr = "", cronExpression = PeriodTimeEnum.EVERY_DAY.cronExpression
                )
                "7" -> Time(
                    id = PeriodTimeEnum.EVERY_WEEK.id, timeEn = "", timeAr = "", cronExpression = PeriodTimeEnum.EVERY_WEEK.cronExpression
                )
                "30" -> Time(
                    id = PeriodTimeEnum.EVERY_MONTH.id, timeEn = "", timeAr = "", cronExpression = PeriodTimeEnum.EVERY_MONTH.cronExpression
                )
                else ->
                    Time(
                    id = PeriodTimeEnum.DAY_AFTER_DAY.id, timeEn = "", timeAr = "", cronExpression = PeriodTimeEnum.DAY_AFTER_DAY.cronExpression
                )
            }
        }
    }


    return periodTime
}
fun getStartingDateFromCronExpression(cronExpression: String): Long? {
    val fields = cronExpression.split(" ")

    val hourField = fields[1]

    val dayOfMonthField = fields[2]

    if (!hourField.contains("/") && dayOfMonthField == "*")
        return null


    val monthField = fields[3]

//    val yearField = fields[5]

    val day = dayOfMonthField.split("/")[0].toInt()
    val month = monthField.split("/")[0].toInt()
//    val year = yearField.split("/")[0].toInt()


    val calendar = Calendar.getInstance().apply {
        set(Calendar.MONTH, month - 1)
        set(Calendar.DAY_OF_MONTH, day)
    }

    return calendar.timeInMillis
}
fun getSpecificDaysFromCronExpression(cronExpression: String): List<Day>? {
    val fields = cronExpression.split(" ")

    val dayOfWeekField = fields[4]

    if (dayOfWeekField == "*")
        return null

    val specificDays = dayOfWeekField.split(",")
    return specificDays.map {
        val day = DayEnum.getDay(it.toInt())
        Day(day.id, it)
    }
}
fun getReminderTimeFromTime(time: String): ReminderTime {
    if(time.isEmpty())
        return ReminderTime(
            "0", "0", "0", "", true, "00:00"
        )
    val isAM = time.lowercase().contains("am")
    val hour12 = time.substringBefore(":")
    val hour24 = ((hour12.toIntOrNull() ?: 0) + 12).toString()
    val minute = time.substringAfter(":").subSequence(0, 2).toString()


    return ReminderTime(
        hour12, hour24, minute, "", isAM, "$hour12:$minute"
    )
}
fun getReminderTimeFromDateTime(dateTime: String): ReminderTime {
    return try {
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        val inputDate = inputDateFormat.parse(dateTime)
        val dateTime = outputDateFormat.format(inputDate)

        val isAM = dateTime.lowercase().contains("am")
        val hour12 = dateTime.substringBefore(":")
        val hour24 = ((hour12.toIntOrNull() ?: 0) + 12).toString()
        val minute = dateTime.substringAfter(":").subSequence(0, 2).toString()


        ReminderTime(
            hour12, hour24, minute, "", isAM, "$hour12:$minute"
        )

    } catch (_: Exception) {
        ReminderTime(
            "0", "0", "0", "", true, "00:00"
        )
    }

    if(dateTime.isEmpty())
        return ReminderTime(
            "0", "0", "0", "", true, "00:00"
        )
}