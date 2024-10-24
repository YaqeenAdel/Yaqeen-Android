package com.cancer.yaqeen.presentation.ui.auth

import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.auth.models.Profile
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.auth.models.UserInterest
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.features.auth.models.UserType
import com.cancer.yaqeen.data.features.onboarding.models.Module
import com.cancer.yaqeen.data.features.onboarding.models.Resources
import com.cancer.yaqeen.data.features.onboarding.models.University
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateProfileRequestBuilder
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.domain.features.auth.login.usecases.LoginUseCase
import com.cancer.yaqeen.domain.features.auth.login.usecases.LogoutUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetResourcesUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetUniversitiesUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetUserProfileUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.UpdateInterestsUserUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.UpdateUserProfileUseCase
import com.cancer.yaqeen.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getResourcesUseCase: GetResourcesUseCase,
    private val getUniversitiesUseCase: GetUniversitiesUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val updateInterestsUserUseCase: UpdateInterestsUserUseCase,
) : ViewModel() {


    private val _viewStateResources = MutableStateFlow<Resources?>(null)
    val viewStateResources = _viewStateResources.asSharedFlow()


    private val _viewStateUniversities = MutableStateFlow<List<University>>(listOf())
    val viewStateUniversities = _viewStateUniversities.asSharedFlow()


    private val _viewStateUpdateProfileSuccess = MutableStateFlow<Boolean?>(null)
    val viewStateUpdateProfileSuccess = _viewStateUpdateProfileSuccess.asSharedFlow()


    private val _viewStateLoginSuccess = MutableStateFlow<User?>(null)
    val viewStateLoginSuccess = _viewStateLoginSuccess.asSharedFlow()


    private val _viewStateUserDataCompleted = SingleLiveEvent<Boolean?>()
    val viewStateUserDataCompleted: LiveData<Boolean?> = _viewStateUserDataCompleted


    private val _viewStateLoading = MutableStateFlow<Boolean>(false)
    val viewStateLoading = _viewStateLoading.asStateFlow()


    private val _viewStateError = MutableStateFlow<ErrorEntity?>(null)
    val viewStateError = _viewStateError.asStateFlow()


    private var userProfile: ObservableField<Profile> = ObservableField(Profile())

    fun selectedLanguageIsEnglish() =
        prefEncryptionUtil.selectedLanguageIsEnglish()
    fun changeLanguage(lang: String): Boolean {
        var isSameLanguage = true
        viewModelScope.launch {
            if(prefEncryptionUtil.selectedLanguage != lang) {
                isSameLanguage = false
                prefEncryptionUtil.selectedLanguage = lang
            }
        }
        return isSameLanguage
    }

    fun getResources() {
        viewModelScope.launch(Dispatchers.IO) {
            getResourcesUseCase().onEach { response ->
                emitLoading(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
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

    fun getUniversities(countryCode: String, stateCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getUniversitiesUseCase(countryCode, stateCode).onEach { response ->
                emitLoading(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        response.data?.let {
                            _viewStateUniversities.emit(it)
                        }
                    }

                    else -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    fun login(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            loginUseCase(context).onEach { response ->
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
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

    private fun logout(context: Context) {
        viewModelScope.launch {
            logoutUseCase(context).onEach { response ->
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        response.data?.let {

                        }
                    }

                    else -> {}
                }
            }.catch {

            }.launchIn(viewModelScope)
        }
    }

    private fun getProfile(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            getUserProfileUseCase().onEach { response ->
//                emitLoading(response.loading)
                when (response.status) {
                    Status.ERROR -> {
                        emitError(response.errorEntity)
                        _viewStateLoginSuccess.emit(user)
                        _viewStateLoginSuccess.emit(null)
                    }

                    Status.SUCCESS -> {
                        response.data?.let {
                            with(it) {
                                if(patient?.cancerTypeID == null || patient.cancerStageID == null || userInterests.isNullOrEmpty()
//                                    || doctor?.medicalField == null || doctor.degree == null || doctor.university == null
                                    ){
                                    _viewStateLoginSuccess.emit(it)
                                }else{
                                    prefEncryptionUtil.isLogged = true
                                    _viewStateUserDataCompleted.postValue(true)
                                }
                                setProfileUser(
                                    doctor == null,
                                    patient?.cancerTypeID,
                                    patient?.ageGroup,
                                    patient?.cancerStageID,
                                    doctor?.medicalField,
                                    doctor?.degree,
                                    doctor?.university,
                                    agreedTerms,
                                    userInterests

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


    fun updateUserProfile(isLoggedIn: Boolean = false) {
        prefEncryptionUtil.isLogged = isLoggedIn
        val user = getUser()
        getUserProfile()?.run {
            viewModelScope.launch(Dispatchers.IO) {
                updateUserProfileUseCase(
                    UpdateProfileRequestBuilder(
                        firstName = user?.firstName ?: "",
                        lastName = user?.lastName ?: "",
                        gender = user?.gender ?: "",
                        interestModuleIds ?: listOf(0),
                        ageGroupPatient = 1,
                        cancerStageIDPatient = stageId,
                        cancerTypeIDPatient = cancerTypeId
                    ).buildRequestBody()

                ).onEach { response ->
                    emitLoading(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
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

//    fun updateInterestsUser(){
//        getUserProfile()?.run {
//            viewModelScope.launch {
//                updateInterestsUserUseCase(
//                    UpdateInterestsUserRequestBuilder(
//                        interestModuleIds
//                    ).buildRequestBody()
//                ).onEach { response ->
//                    emitLoading(response.loading)
//                    when (response.status) {
//                        Status.ERROR -> emitError(response.errorEntity)
//                        Status.SUCCESS -> {
//                            Log.d("TAG", "updateInterestsUser: ${response.data}")
//                            response.data?.let {
//                                _viewStateUpdateProfileSuccess.emit(true)
//                                _viewStateUpdateProfileSuccess.emit(null)
//                            }
//                        }
//
//                        else -> {}
//                    }
//                }.launchIn(viewModelScope)
//            }
//        }
//    }

    private suspend fun emitLoading(isLoading: Boolean) {
        withContext(Dispatchers.Main) {
            _viewStateLoading.emit(isLoading)
        }
    }

    private suspend fun emitError(errorEntity: ErrorEntity?) {
        withContext(Dispatchers.Main) {
            _viewStateError.emit(errorEntity)
            _viewStateError.emit(null)
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
        agreedTerms: Boolean?,
        userInterests: List<UserInterest>
    ) {
        userProfile.set(
            Profile(
                userType = if (isPatient)
                    UserType.PATIENT
                else
                    UserType.DOCTOR,
                cancerTypeId = cancerTypeID,
                stageId = cancerStageID,
                interestModuleIds = userInterests.map { it.id } as? MutableList<Int> ?: arrayListOf(),
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

    fun selectInterestModule(module: Module){
        userProfile.get()?.also {
            val id = module.id
            if (module.selected)
                it.interestModuleIds?.add(id)
            else
                it.interestModuleIds?.remove(id)
        }
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

     fun getUser(): User? =
    prefEncryptionUtil.getModelData(SharedPrefEncryptionUtil.PREF_USER, User::class.java)

}