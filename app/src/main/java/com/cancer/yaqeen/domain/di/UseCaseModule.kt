package com.cancer.yaqeen.domain.di

import com.cancer.yaqeen.data.features.auth.AuthRepositoryImpl
import com.cancer.yaqeen.data.features.home.articles.HomeRepositoryImpl
import com.cancer.yaqeen.data.features.onboarding.OnboardingRepositoryImpl
import com.cancer.yaqeen.data.features.profile.ProfileRepositoryImpl
import com.cancer.yaqeen.data.features.home.schedule.ScheduleRepositoryImpl
import com.cancer.yaqeen.domain.features.auth.login.usecases.LoginUseCase
import com.cancer.yaqeen.domain.features.auth.login.usecases.LogoutUseCase
import com.cancer.yaqeen.domain.features.auth.login.usecases.RefreshTokenUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.BookmarkArticleUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.GetArticlesUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.GetBookmarkedArticlesUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.GetLocalBookmarkedArticlesUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.RemoveBookmarkedArticleUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.RemoveBookmarkedArticlesUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.SaveArticleUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.UnBookmarkArticleUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.AddMedicalReminderUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.DeleteScheduleUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.DeleteSymptomFromScheduleUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.EditLocalMedicalAppointmentUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.EditMedicalReminderUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.GetLocalMedicalAppointmentUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.GetMedicalRemindersUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.RemoveLocalMedicalAppointmentUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.RemoveLocalMedicalAppointmentsUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.SaveLocalMedicalAppointmentUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.AddMedicationUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.EditLocalMedicationUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.EditMedicationUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.GetLocalMedicationUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.GetLocalMedicationsUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.GetTodayRemindersUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.GetMedicationRemindersUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.RemoveLocalMedicationUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.RemoveLocalMedicationsUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.SaveLocalMedicationUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.AddRoutineTestUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.AddRoutineTestWithoutPhotoUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.EditLocalRoutineTestUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.EditRoutineTestUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.EditRoutineTestWithoutPhotoUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.GetLocalRoutineTestUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.GetLocalRoutineTestsUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.GetRoutineTestsUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.RemoveLocalRoutineTestUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.RemoveLocalRoutineTestsUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.SaveLocalRoutineTestUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.AddSymptomUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.AddSymptomWithoutPhotoUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.DeleteSymptomUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.EditSymptomUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.EditSymptomWithoutUploadUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.GetSymptomsTypesUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.GetSymptomsUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetResourcesUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetUniversitiesUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetUserProfileUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.UpdateInterestsUserUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.UpdateUserProfileUseCase
import com.cancer.yaqeen.domain.features.profile.GetProfileUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Singleton
    @Provides
    fun provideLoginUseCase(repository: AuthRepositoryImpl) =
        LoginUseCase(repository)

    @Singleton
    @Provides
    fun provideLogoutUseCase(repository: AuthRepositoryImpl) =
        LogoutUseCase(repository)

    @Singleton
    @Provides
    fun provideRefreshTokenUseCase(repository: AuthRepositoryImpl) =
        RefreshTokenUseCase(repository)

    @Singleton
    @Provides
    fun provideGetResourcesUseCase(repository: OnboardingRepositoryImpl) =
        GetResourcesUseCase(repository)

    @Singleton
    @Provides
    fun provideGetUniversitiesUseCase(repository: OnboardingRepositoryImpl) =
        GetUniversitiesUseCase(repository)

    @Singleton
    @Provides
    fun provideUpdateUserProfileUseCase(repository: OnboardingRepositoryImpl) =
        UpdateUserProfileUseCase(repository)

    @Singleton
    @Provides
    fun provideUpdateInterestsUserUseCase(repository: OnboardingRepositoryImpl) =
        UpdateInterestsUserUseCase(repository)

    @Singleton
    @Provides
    fun provideGetUserProfileUseCase(repository: OnboardingRepositoryImpl) =
        GetUserProfileUseCase(repository)
    @Singleton
    @Provides
    fun provideGetProfileUseCase(repository: ProfileRepositoryImpl) =
        GetProfileUseCase(repository)
    @Singleton
    @Provides
    fun provideGetArticleUseCase(repository: HomeRepositoryImpl) =
        GetArticlesUseCase(repository)
    @Singleton
    @Provides
    fun provideBookmarkArticleUseCase(repository: HomeRepositoryImpl) =
        BookmarkArticleUseCase(repository)
    @Singleton
    @Provides
    fun provideGetBookmarkedArticlesUseCase(repository: HomeRepositoryImpl) =
        GetBookmarkedArticlesUseCase(repository)
    @Singleton
    @Provides
    fun provideUnBookmarkArticleUseCase(repository: HomeRepositoryImpl) =
        UnBookmarkArticleUseCase(repository)
    @Singleton
    @Provides
    fun provideGetLocalBookmarkedArticlesUseCase(repository: HomeRepositoryImpl) =
        GetLocalBookmarkedArticlesUseCase(repository)
    @Singleton
    @Provides
    fun provideRemoveBookmarkedArticlesUseCase(repository: HomeRepositoryImpl) =
        RemoveBookmarkedArticlesUseCase(repository)
    @Singleton
    @Provides
    fun provideRemoveBookmarkedArticleUseCase(repository: HomeRepositoryImpl) =
        RemoveBookmarkedArticleUseCase(repository)
    @Singleton
    @Provides
    fun provideSaveArticleUseCase(repository: HomeRepositoryImpl) =
        SaveArticleUseCase(repository)
    @Singleton
    @Provides
    fun provideAddMedicationUseCase(repository: ScheduleRepositoryImpl) =
        AddMedicationUseCase(repository)
    @Singleton
    @Provides
    fun provideGetMedicationRemindersUseCase(repository: ScheduleRepositoryImpl) =
        GetMedicationRemindersUseCase(repository)
    @Singleton
    @Provides
    fun provideEditMedicationUseCase(repository: ScheduleRepositoryImpl) =
        EditMedicationUseCase(repository)
    @Singleton
    @Provides
    fun provideGetMedicationRemindersFromNowUseCase(repository: ScheduleRepositoryImpl) =
        GetTodayRemindersUseCase(repository)
    @Singleton
    @Provides
    fun provideGetSymptomsTypesUseCase(repository: ScheduleRepositoryImpl) =
        GetSymptomsTypesUseCase(repository)

    @Singleton
    @Provides
    fun provideAddSymptomUseCase(repository: ScheduleRepositoryImpl) =
        AddSymptomUseCase(repository)

    @Singleton
    @Provides
    fun provideAddSymptomWithoutPhotoUseCase(repository: ScheduleRepositoryImpl) =
        AddSymptomWithoutPhotoUseCase(repository)
    @Singleton
    @Provides
    fun provideEditSymptomUseCase(repository: ScheduleRepositoryImpl) =
        EditSymptomUseCase(repository)
    @Singleton
    @Provides
    fun provideEditSymptomWithoutUploadUseCase(repository: ScheduleRepositoryImpl) =
        EditSymptomWithoutUploadUseCase(repository)
    @Singleton
    @Provides
    fun provideDeleteSymptomUseCase(repository: ScheduleRepositoryImpl) =
        DeleteSymptomUseCase(repository)
    @Singleton
    @Provides
    fun provideGetSymptomsUseCase(repository: ScheduleRepositoryImpl) =
        GetSymptomsUseCase(repository)
    @Singleton
    @Provides
    fun provideAddMedicalReminderUseCase(repository: ScheduleRepositoryImpl) =
        AddMedicalReminderUseCase(repository)
    @Singleton
    @Provides
    fun provideEditMedicalReminderUseCase(repository: ScheduleRepositoryImpl) =
        EditMedicalReminderUseCase(repository)
    @Singleton
    @Provides
    fun provideGetMedicalRemindersUseCase(repository: ScheduleRepositoryImpl) =
        GetMedicalRemindersUseCase(repository)

    @Singleton
    @Provides
    fun provideDeleteSymptomFromScheduleUseCase(repository: ScheduleRepositoryImpl) =
        DeleteSymptomFromScheduleUseCase(repository)

    @Singleton
    @Provides
    fun provideDeleteScheduleUseCase(repository: ScheduleRepositoryImpl) =
        DeleteScheduleUseCase(repository)

    @Singleton
    @Provides
    fun provideAddRoutineTestUseCase(repository: ScheduleRepositoryImpl) =
        AddRoutineTestUseCase(repository)
    @Singleton
    @Provides
    fun provideAddRoutineTestWithoutPhotoUseCase(repository: ScheduleRepositoryImpl) =
        AddRoutineTestWithoutPhotoUseCase(repository)

    @Singleton
    @Provides
    fun provideEditRoutineTestUseCase(repository: ScheduleRepositoryImpl) =
        EditRoutineTestUseCase(repository)

    @Singleton
    @Provides
    fun provideEditRoutineTestWithoutPhotoUseCase(repository: ScheduleRepositoryImpl) =
        EditRoutineTestWithoutPhotoUseCase(repository)
    @Singleton
    @Provides
    fun provideGetRoutineTestsUseCase(repository: ScheduleRepositoryImpl) =
        GetRoutineTestsUseCase(repository)
    @Singleton
    @Provides
    fun provideSaveLocalMedicationUseCase(repository: ScheduleRepositoryImpl) =
        SaveLocalMedicationUseCase(repository)
    @Singleton
    @Provides
    fun provideSaveLocalRoutineTestUseCase(repository: ScheduleRepositoryImpl) =
        SaveLocalRoutineTestUseCase(repository)
    @Singleton
    @Provides
    fun provideSaveLocalMedicalAppointmentUseCase(repository: ScheduleRepositoryImpl) =
        SaveLocalMedicalAppointmentUseCase(repository)
    @Singleton
    @Provides
    fun provideRemoveLocalMedicationUseCase(repository: ScheduleRepositoryImpl) =
        RemoveLocalMedicationUseCase(repository)
    @Singleton
    @Provides
    fun provideRemoveLocalRoutineTestUseCase(repository: ScheduleRepositoryImpl) =
        RemoveLocalRoutineTestUseCase(repository)
    @Singleton
    @Provides
    fun provideRemoveLocalMedicalAppointmentUseCase(repository: ScheduleRepositoryImpl) =
        RemoveLocalMedicalAppointmentUseCase(repository)
    @Singleton
    @Provides
    fun provideEditLocalMedicationsUseCase(repository: ScheduleRepositoryImpl) =
        EditLocalMedicationUseCase(repository)
    @Singleton
    @Provides
    fun provideEditLocalRoutineTestsUseCase(repository: ScheduleRepositoryImpl) =
        EditLocalRoutineTestUseCase(repository)
    @Singleton
    @Provides
    fun provideEditLocalMedicalAppointmentsUseCase(repository: ScheduleRepositoryImpl) =
        EditLocalMedicalAppointmentUseCase(repository)
    @Singleton
    @Provides
    fun provideRemoveLocalMedicationsUseCase(repository: ScheduleRepositoryImpl) =
        RemoveLocalMedicationsUseCase(repository)
    @Singleton
    @Provides
    fun provideRemoveLocalRoutineTestsUseCase(repository: ScheduleRepositoryImpl) =
        RemoveLocalRoutineTestsUseCase(repository)
    @Singleton
    @Provides
    fun provideRemoveLocalMedicalAppointmentsUseCase(repository: ScheduleRepositoryImpl) =
        RemoveLocalMedicalAppointmentsUseCase(repository)
    @Singleton
    @Provides
    fun provideGetLocalMedicationUseCase(repository: ScheduleRepositoryImpl) =
        GetLocalMedicationUseCase(repository)
    @Singleton
    @Provides
    fun provideGetLocalMedicationsUseCase(repository: ScheduleRepositoryImpl) =
        GetLocalMedicationsUseCase(repository)
    @Singleton
    @Provides
    fun provideGetLocalRoutineTestUseCase(repository: ScheduleRepositoryImpl) =
        GetLocalRoutineTestUseCase(repository)
    @Singleton
    @Provides
    fun provideGetLocalRoutineTestsUseCase(repository: ScheduleRepositoryImpl) =
        GetLocalRoutineTestsUseCase(repository)
    @Singleton
    @Provides
    fun provideGetLocalMedicalAppointmentUseCase(repository: ScheduleRepositoryImpl) =
        GetLocalMedicalAppointmentUseCase(repository)


}