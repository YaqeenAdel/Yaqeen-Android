package com.cancer.yaqeen.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cancer.yaqeen.data.features.home.articles.room.Article
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.medication.room.ReminderStatus
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

    @Query("SELECT * FROM Medication WHERE medicationId = :medicationId")
    suspend fun selectMedication(medicationId: Int): MedicationDB?

//    @Query("SELECT * FROM Medication")
//    suspend fun selectMedications(): List<MedicationDB>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedication(medication: MedicationDB): Long

    @Update
    suspend fun updateMedication(medication: MedicationDB): Int

    @Query("UPDATE Medication SET workID = :workID, workSpecificDaysIDs = :workSpecificDaysIDs, isReminded = :isReminded, startDateTime = :startDateTime WHERE medicationId = :medicationId")
    suspend fun updateMedication(workID: String? = null, workSpecificDaysIDs: List<String> = listOf(), isReminded: Boolean, startDateTime: Long, medicationId: Int): Int

    @Query("DELETE FROM Medication WHERE medicationId = :medicationId")
    suspend fun deleteMedication(medicationId: Int): Int

    @Query("DELETE FROM Medication")
    suspend fun deleteMedications(): Int

    @Query("SELECT * FROM Medication")
    suspend fun selectMedications(): List<MedicationDB>?

    @Query("SELECT * FROM Medication WHERE statusReminder = :statusReminder")
    suspend fun selectRemindedMedications(statusReminder: ReminderStatus = ReminderStatus.REMINDED): List<MedicationDB>?

    @Query("SELECT * FROM RoutineTest WHERE routineTestId = :routineTestId")
    suspend fun selectRoutineTest(routineTestId: Int): RoutineTestDB?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineTest(routineTest: RoutineTestDB): Long

    @Update
    suspend fun updateRoutineTest(routineTest: RoutineTestDB): Int

    @Query("DELETE FROM RoutineTest WHERE routineTestId = :routineTestId")
    suspend fun deleteRoutineTest(routineTestId: Int): Int

    @Query("DELETE FROM RoutineTest")
    suspend fun deleteRoutineTests(): Int

    @Query("SELECT * FROM RoutineTest WHERE statusReminder = :statusReminder")
    suspend fun selectRemindedRoutineTests(statusReminder: ReminderStatus = ReminderStatus.REMINDED): List<RoutineTestDB>?

    @Query("SELECT * FROM RoutineTest")
    suspend fun selectRoutineTests(): List<RoutineTestDB>?

    @Query("SELECT * FROM MedicalAppointment WHERE medicalAppointmentId = :medicalAppointmentId")
    suspend fun selectMedicalAppointment(medicalAppointmentId: Int): MedicalAppointmentDB?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalAppointment(medicalAppointment: MedicalAppointmentDB): Long

    @Update
    suspend fun updateMedicalAppointment(medicalAppointment: MedicalAppointmentDB): Int

    @Query("DELETE FROM MedicalAppointment WHERE medicalAppointmentId = :medicalAppointmentId")
    suspend fun deleteMedicalAppointment(medicalAppointmentId: Int): Int

    @Query("DELETE FROM MedicalAppointment")
    suspend fun deleteMedicalAppointments(): Int


}