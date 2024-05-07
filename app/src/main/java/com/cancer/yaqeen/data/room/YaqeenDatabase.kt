package com.cancer.yaqeen.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cancer.yaqeen.data.features.home.articles.room.Article
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.data.room.converters.IntConverters
import com.cancer.yaqeen.data.room.converters.UUIDConverters

@Database(entities = [Article::class, MedicationDB::class, RoutineTestDB::class, MedicalAppointmentDB::class], version = 24, exportSchema = false)
@TypeConverters(IntConverters::class, UUIDConverters::class)
abstract class YaqeenDatabase: RoomDatabase() {

    abstract fun yaqeenDao(): YaqeenDao

}