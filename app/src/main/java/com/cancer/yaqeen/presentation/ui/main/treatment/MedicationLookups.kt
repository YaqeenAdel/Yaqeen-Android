package com.cancer.yaqeen.presentation.ui.main.treatment

import android.content.Context
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.MedicationType
import com.cancer.yaqeen.data.features.home.schedule.medication.models.MedicationTypeEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.models.UnitType
import com.cancer.yaqeen.data.features.home.schedule.medication.models.UnitTypeEnum

fun getMedicationTypes(context: Context) =
    listOf(
        MedicationType(
            id = MedicationTypeEnum.CAPSULE.id, name = context.getString(R.string.capsule), iconResId = MedicationTypeEnum.CAPSULE.iconId
        ),
        MedicationType(
            id = MedicationTypeEnum.PILLS.id, name = context.getString(R.string.pills), iconResId = MedicationTypeEnum.PILLS.iconId
        ),
        MedicationType(
            id = MedicationTypeEnum.LIQUID.id, name = context.getString(R.string.liquid), iconResId = MedicationTypeEnum.LIQUID.iconId
        ),
        MedicationType(
            id = MedicationTypeEnum.INJECTION.id, name = context.getString(R.string.injection), iconResId = MedicationTypeEnum.INJECTION.iconId
        )
    )

fun getMedicationType(context: Context, medicationType: String): MedicationType?{
    return when(medicationType){
        context.getString(R.string.capsule_enum) -> MedicationType(
            id = MedicationTypeEnum.CAPSULE.id, name = context.getString(R.string.capsule), iconResId = MedicationTypeEnum.CAPSULE.iconId
        )
        context.getString(R.string.pills_enum) -> MedicationType(
            id = MedicationTypeEnum.PILLS.id, name = context.getString(R.string.pills), iconResId = MedicationTypeEnum.PILLS.iconId
        )
        context.getString(R.string.liquid_enum) -> MedicationType(
            id = MedicationTypeEnum.LIQUID.id, name = context.getString(R.string.liquid), iconResId = MedicationTypeEnum.LIQUID.iconId
        )
        context.getString(R.string.injection_enum) -> MedicationType(
            id = MedicationTypeEnum.INJECTION.id, name = context.getString(R.string.injection), iconResId = MedicationTypeEnum.INJECTION.iconId
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