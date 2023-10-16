package com.cancer.yaqeen.presentation.ui.onboarding

import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.auth.models.Profile
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.features.auth.models.UserType
import com.cancer.yaqeen.data.features.onboarding.models.Resources
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateProfileRequestBuilder
import com.cancer.yaqeen.data.features.onboarding.responses.UpdateProfileResponse
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.domain.features.auth.login.usecases.LoginUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetResourcesUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetUserProfileUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.UpdateUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
    private val loginUseCase: LoginUseCase,
    private val getResourcesUseCase: GetResourcesUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
) : ViewModel() {


    private val _viewStateResources = MutableStateFlow<Resources?>(null)
    val viewStateResources = _viewStateResources.asSharedFlow()


    private val _viewStateUpdateProfileSuccess = MutableStateFlow<Boolean?>(null)
    val viewStateUpdateProfileSuccess = _viewStateUpdateProfileSuccess.asSharedFlow()


    private val _viewStateLoginSuccess = MutableStateFlow<User?>(null)
    val viewStateLoginSuccess = _viewStateLoginSuccess.asSharedFlow()


    private val _viewStateLoading = MutableStateFlow<Boolean>(false)
    val viewStateLoading = _viewStateLoading.asStateFlow()


    private val _viewStateError = MutableStateFlow<ErrorEntity?>(null)
    val viewStateError = _viewStateError.asStateFlow()


    private var userProfile: ObservableField<Profile> = ObservableField(Profile())

    fun getResources() {
        viewModelScope.launch {
            getResourcesUseCase().onEach { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> _viewStateError.emit(response.errorEntity)
                    Status.SUCCESS -> {
                        response.data?.let {
                            _viewStateResources.emit(it)
                        }
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    fun login(context: Context) {
        viewModelScope.launch {
            loginUseCase(context).onEach { response ->
                when (response.status) {
                    Status.ERROR -> _viewStateError.emit(response.errorEntity)
                    Status.SUCCESS -> {
                        response.data?.let {
                            getProfile(it)
                        }
                    }

                    else -> {}
                }
            }.catch {

            }.launchIn(viewModelScope)
        }
    }

    private fun getProfile(user: User) {
        viewModelScope.launch {
            getUserProfileUseCase().onEach { response ->
//                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> {
                        _viewStateError.emit(response.errorEntity)
                        _viewStateLoginSuccess.emit(user)
                        _viewStateLoginSuccess.emit(null)
                    }

                    Status.SUCCESS -> {
                        response.data?.let {
                            _viewStateLoginSuccess.emit(it)
                            with(it) {
                                setProfileUser(
                                    doctor == null,
                                    patient?.cancerTypeID,
                                    patient?.ageGroup,
                                    patient?.cancerStageID,
                                    doctor?.medicalField,
                                    doctor?.degree,
                                    doctor?.university,
                                    agreedTerms
                                )
                            }

                            _viewStateLoginSuccess.emit(null)
                        }
                    }

                    else -> {}
                }
            }.catch {

            }.launchIn(viewModelScope)
        }
    }


    fun updateUserProfile() {
        getUserProfile()?.run {
            viewModelScope.launch {
                updateUserProfileUseCase(

                    UpdateProfileRequestBuilder(
                        isPatient = userType == UserType.PATIENT,
                        agreedTerms = true,
//                        aggregateCountDoctor = 0,
                        degreeIDDoctor = degreeId,
                        medicalFieldIDDoctor = medicalFieldId,
                        universityIDDoctor = universityId,
//                        verificationNotesDoctor = "",
//                        verifierUserIDDoctor = 0,
                        ageGroupPatient = 2,
                        cancerStageIDPatient = stageId,
                        cancerTypeIDPatient = cancerTypeId,
//                        questionsAggregateCount = 0
                    ).buildRequestBody()

                ).onEach { response ->
                    _viewStateLoading.emit(response.loading)
                    when (response.status) {
                        Status.ERROR -> _viewStateError.emit(response.errorEntity)
                        Status.SUCCESS -> {
                            Log.d("TAG", "updateUserProfile: ${response.data}")
                            response.data?.let {
                                _viewStateUpdateProfileSuccess.emit(true)
                                _viewStateUpdateProfileSuccess.emit(null)
                            }
                        }

                        else -> {}
                    }
                }.launchIn(viewModelScope)
            }
        }
    }

    private fun setProfileUser(
        isPatient: Boolean,
        cancerTypeID: Int?,
        ageGroup: Int?,
        cancerStageID: Int?,
        medicalField: String?,
        degree: String?,
        university: String?,
        agreedTerms: Boolean?
    ) {
        userProfile.set(
            Profile(
                userType = if (isPatient)
                    UserType.PATIENT
                else
                    UserType.DOCTOR,
                cancerTypeId = cancerTypeID,
                stageId = cancerStageID,
                interestModuleId = ageGroup,
                universityId = university,
                degreeId = degree,
                medicalFieldId = medicalField,
                agreedTerms = agreedTerms ?: false,
            )
        )
    }

    fun selectUser(isPatient: Boolean) =
        userProfile.set(
            Profile(
                userType = if (isPatient)
                    UserType.PATIENT
                else
                    UserType.DOCTOR
            )
        )


    fun getUserTypeSelected(): UserType =
        userProfile.get()?.userType ?: UserType.PATIENT

    fun selectCancerType(cancerTypeId: Int) =
        userProfile.get()?.also {
            it.cancerTypeId = cancerTypeId
        }

    fun selectStage(stageId: Int) =
        userProfile.get()?.also {
            it.stageId = stageId
        }

    fun selectInterestModule(interestModuleId: Int) =
        userProfile.get()?.also {
            it.interestModuleId = interestModuleId
        }

    fun selectUniversity(universityId: String) =
        userProfile.get()?.also {
            it.universityId = universityId
        }

    fun selectDegree(degreeId: String) =
        userProfile.get()?.also {
            it.degreeId = degreeId
        }

    fun selectMedicalField(medicalFieldId: String) =
        userProfile.get()?.also {
            it.medicalFieldId = medicalFieldId
        }

    fun getUserProfile(): Profile? =
        userProfile.get()
}