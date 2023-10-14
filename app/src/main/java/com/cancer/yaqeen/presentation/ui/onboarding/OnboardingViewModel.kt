package com.cancer.yaqeen.presentation.ui.onboarding

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.features.auth.models.UserType
import com.cancer.yaqeen.data.features.onboarding.models.Resources
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetResourcesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val getResourcesUseCase: GetResourcesUseCase
): ViewModel() {


    private val _viewStateResources = MutableStateFlow<Resources>(
        Resources(
            listOf(),
            listOf(),
            listOf(),
            listOf()
        )
    )
    val viewStateResources = _viewStateResources.asSharedFlow()


    private val _viewStateLoading = MutableStateFlow<Boolean>(false)
    val viewStateLoading = _viewStateLoading.asStateFlow()


    private val _viewStateError = MutableStateFlow<ErrorEntity?>(null)
    val viewStateError = _viewStateError.asStateFlow()


    private var userTypeSelected: ObservableField<UserType> = ObservableField(UserType.PATIENT)

    fun getResources(){
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

    fun selectUser(isPatient: Boolean){
        userTypeSelected.set(
            if(isPatient)
                UserType.PATIENT
            else
                UserType.DOCTOR
        )
    }

    fun getUserTypeSelected(): UserType =
        userTypeSelected.get() ?: UserType.PATIENT
}