package com.cancer.yaqeen.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cancer.yaqeen.data.features.home.articles.room.Article
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB

@Database(entities = [Article::class, MedicationDB::class, RoutineTestDB::class, MedicalAppointmentDB::class], version = 8, exportSchema = false)
abstract class YaqeenDatabase: RoomDatabase() {

    abstract fun yaqeenDao(): YaqeenDao

}