package com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.modules

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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

        binding.tvNext.setOnClickListener {
            navController.tryNavigate(
                ModulesFragmentDirections.actionModulesFragmentToTermsAndConditionFragment()
            )
        }

        binding.tvBack.setOnClickListener {
            navController.tryPopBackStack()
        }

        observeStates()
    }
    private fun observeStates() {
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
        }

        binding.rvModules.apply {
            adapter = modulesAdapter
            addItemDecoration(
                GridMarginItemDecoration(
                    dpToPx(16f, requireContext())
                )
            )
        }

//        modulesAdapter.submitList(
//            listOf(
//                Module(
//                    id = 1, moduleName = "Stage"
//                ),
//                Module(
//                    id = 2, moduleName = "Stage"
//                ),
//                Module(
//                    id = 3, moduleName = "Stage"
//                ),
//                Module(
//                    id = 4, moduleName = "Stage"
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