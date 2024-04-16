package com.cancer.yaqeen.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cancer.yaqeen.data.features.home.articles.room.Article
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB

@Dao
interface YaqeenDao {

    @Query("SELECT * FROM Article")
    suspend fun getArticles(): List<Article>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: Article): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<Article>): List<Long>

    @Query("DELETE FROM Article WHERE articleID = :articleID")
    suspend fun removeArticle(articleID: Int): Int

    @Query("DELETE FROM Article")
    suspend fun removeArticles(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: MedicationDB): Long

    @Query("DELETE FROM Medication WHERE medicationId = :medicationId")
    suspend fun deleteMedication(medicationId: Int): Int

    @Query("DELETE FROM Medication")
    suspend fun deleteMedications(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineTest(routineTest: RoutineTestDB): Long

    @Query("DELETE FROM RoutineTest WHERE routineTestId = :routineTestId")
    suspend fun deleteRoutineTest(routineTestId: Int): Int

    @Query("DELETE FROM RoutineTest")
    suspend fun deleteRoutineTests(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalAppointment(medicalAppointment: MedicalAppointmentDB): Long

    @Query("DELETE FROM MedicalAppointment WHERE medicalAppointmentId = :medicalAppointmentId")
    suspend fun deleteMedicalAppointment(medicalAppointmentId: Int): Int

    @Query("DELETE FROM MedicalAppointment")
    suspend fun deleteMedicalAppointments(): Int


}