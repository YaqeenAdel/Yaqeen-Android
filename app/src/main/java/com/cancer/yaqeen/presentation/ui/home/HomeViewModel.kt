package com.cancer.yaqeen.presentation.ui.home

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.auth.models.Profile
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.auth.models.UserType
import com.cancer.yaqeen.data.features.onboarding.models.Resources
import com.cancer.yaqeen.data.features.onboarding.models.University
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.domain.features.home.articles.usecases.GetArticlesUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetUserProfileUseCase
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
class HomeViewModel @Inject constructor(
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
     private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getArticlesUseCase: GetArticlesUseCase
) : ViewModel() {


    private val _viewStateResources = MutableStateFlow<Resources?>(null)
    val viewStateResources = _viewStateResources.asSharedFlow()


    private val _viewStateUniversities = MutableStateFlow<List<University>>(listOf())
    val viewStateUniversities = _viewStateUniversities.asSharedFlow()
    private val _viewStateArticles = MutableStateFlow<List<com.cancer.yaqeen.data.features.home.responses.Article>>(listOf())
    val viewStateArticles = _viewStateArticles.asSharedFlow()

    private val _viewStateUpdateProfileSuccess = MutableStateFlow<Boolean?>(null)
    val viewStateUpdateProfileSuccess = _viewStateUpdateProfileSuccess.asSharedFlow()


    private val _viewStateUserDataCompleted = MutableStateFlow<Boolean?>(null)
    val viewStateUserDataCompleted = _viewStateUserDataCompleted.asSharedFlow()


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
    fun getArticles(){
        viewModelScope.launch {
            getArticlesUseCase().also {response->
                response.collect(){
                    _viewStateLoading.emit(it.loading)

                    when(it.status){
                        Status.ERROR -> emitError(it.errorEntity)
                        Status.SUCCESS -> {
                            it.data?.let {
                                Log.e("articles",it.Articles.size.toString())

                                _viewStateArticles.emit(it.Articles)
                            }
                        }

                        else -> {}
                    }

                }

            }
        }
    }
    private suspend fun emitError(errorEntity: ErrorEntity?) {
        _viewStateError.emit(errorEntity)
        _viewStateError.emit(null)
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






    fun getUserProfile(): Profile? =
        userProfile.get()

     fun getUser(): User? =
    prefEncryptionUtil.getModelData(SharedPrefEncryptionUtil.PREF_USER, User::class.java)

}