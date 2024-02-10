package com.cancer.yaqeen.presentation.ui.main.treatment

import android.content.Context
import android.util.Log
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
import java.time.LocalDate
import java.util.Calendar
import java.util.Date

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

    val minute = fields[0]

    val hourField = fields[1]

    // if time is every 8 or 12 days
    val hour24 = if (hourField.contains("/")){
        val hourFields = hourField.split("/")
        hourFields[0]
    }else {
        hourField
    }

    val (hour12, isAM) = if (hour24.toInt() < 12){
        hour24 to true
    }else {
        (hour24.toInt() - 12).toString() to false
    }

    return ReminderTime(
        hour12, hour24, minute, "", isAM, "$hour12:$minute"
    )
}

fun getPeriodTimeFromCronExpression(cronExpression: String): Time {
    val fields = cronExpression.split(" ")

    val hourField = fields[1]

    // if time is every 8 or 12 days
    val periodTime: Time = if (hourField.contains("/")){
        val hourFields = hourField.split("/")
        if (hourFields[1] == "8")
            Time(
                id = PeriodTimeEnum.EVERY_8_HOURS.id, time = "", cronExpression = PeriodTimeEnum.EVERY_8_HOURS.cronExpression
            )
        else
            Time(
                id = PeriodTimeEnum.EVERY_12_HOURS.id, time = "", cronExpression = PeriodTimeEnum.EVERY_12_HOURS.cronExpression
            )

    }else {
        val dayOfMonthField = fields[2]

        if(dayOfMonthField == "*")
            Time(
                id = PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id, time = "", cronExpression = PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.cronExpression
            )
        else{
            val isEveryDay = dayOfMonthField.split("/")[1] == "1"
            if (isEveryDay)
                Time(
                    id = PeriodTimeEnum.EVERY_DAY.id, time = "", cronExpression = PeriodTimeEnum.EVERY_DAY.cronExpression
                )
            else
                Time(
                    id = PeriodTimeEnum.DAY_AFTER_DAY.id, time = "", cronExpression = PeriodTimeEnum.DAY_AFTER_DAY.cronExpression
                )
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