package com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.modules

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.auth.models.UserType
import com.cancer.yaqeen.data.features.onboarding.models.Module
import com.cancer.yaqeen.data.features.onboarding.models.Stage
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentModulesBinding
import com.cancer.yaqeen.databinding.FragmentStagesBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.onboarding.OnboardingViewModel
import com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.patient.stages.StagesAdapter
import com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.patient.stages.StagesFragmentDirections
import com.cancer.yaqeen.presentation.ui.onboarding.terms_condition.TermsAndConditionFragmentDirections
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.CenterGridMarginItemDecoration
import com.cancer.yaqeen.presentation.util.recyclerview.GridMarginItemDecoration
import com.cancer.yaqeen.presentation.util.recyclerview.HorizontalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ModulesFragment : BaseFragment() {

    private var binding: FragmentModulesBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var modulesAdapter: ModulesAdapter


    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentModulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setupModulesAdapter()

        updateUI()

        setListener()

        observeStates()
    }

    private fun setListener(){
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnNext.setOnClickListener {
            viewModel.updateUserProfile()
        }

        binding.btnPrevious.setOnClickListener {
            navController.tryPopBackStack()
        }
    }
    private fun updateUI() {
        val spannable = SpannableStringBuilder("4/4")
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary_color)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvPageNumber.text = spannable

    }

    private fun observeStates() {
        lifecycleScope {
            viewModel.viewStateLoading.collectLatest {
                onLoading(it)
            }
        }
        lifecycleScope {
            viewModel.viewStateResources.collectLatest {
                it?.let {
                    val modules = when(viewModel.getUserTypeSelected()){
                        UserType.PATIENT -> it.patientInterests
                        UserType.DOCTOR -> it.doctorInterests
                    }
                    modulesAdapter.submitList(modules)
                    modulesAdapter.currentList.firstOrNull()?.apply {
                        val interestModuleId = viewModel.getUserProfile()?.interestModuleId
                        if(interestModuleId == null)
                            selectInterestModule(this)
                        else
                            modulesAdapter.selectItem(interestModuleId)
                    }
                }

            }
        }
        lifecycleScope {
            viewModel.viewStateUpdateProfileSuccess.collectLatest {
                it?.let {
                    navController.tryNavigate(
                        ModulesFragmentDirections.actionModulesFragmentToTermsAndConditionFragment()
                    )
                }
            }
        }
    }

    private fun handleResponseError(errorEntity: ErrorEntity?) {
        val errorMessage = handleError(errorEntity)
        displayErrorMessage(errorMessage)
    }

    private fun displayErrorMessage(errorMessage: String?) {
        errorMessage?.let {
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
    private fun setupModulesAdapter() {
        modulesAdapter = ModulesAdapter {
            selectInterestModule(it)
            viewModel.updateUserProfile()
        }

        binding.rvModules.apply {
            adapter = modulesAdapter
//            addItemDecoration(
//                CenterGridMarginItemDecoration(
//                    2,
//                    dpToPx(1f, requireContext())
//                )
//            )
        }

//        modulesAdapter.submitList(
//            listOf(
//                Module(
//                    id = 1, moduleName = "Awareness"
//                ),
//                Module(
//                    id = 2, moduleName = "Connecting for consultant"
//                ),
//                Module(
//                    id = 3, moduleName = "Tracking Health"
//                ),
//                Module(
//                    id = 4, moduleName = "Emotional support\n"
//                )
//            )
//        )
//        modulesAdapter.currentList.firstOrNull()?.apply {
//            selectInterestModule(this)
//        }
    }

    private fun selectInterestModule(it: Module) {
        viewModel.selectInterestModule(it.id.toInt())
    }
}