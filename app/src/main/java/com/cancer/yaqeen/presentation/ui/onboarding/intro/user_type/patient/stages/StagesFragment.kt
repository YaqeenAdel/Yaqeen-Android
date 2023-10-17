package com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.patient.stages

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
import com.cancer.yaqeen.data.features.onboarding.models.CancerType
import com.cancer.yaqeen.data.features.onboarding.models.Stage
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentStagesBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.onboarding.OnboardingViewModel
import com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.patient.cancer_type.CancerTypesAdapter
import com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.patient.cancer_type.SelectCancerTypeFragmentDirections
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.HorizontalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class StagesFragment : BaseFragment() {

    private var binding: FragmentStagesBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var stagesAdapter: StagesAdapter

    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setupStagesAdapter()

        binding.tvNext.setOnClickListener {
            viewModel.updateUserProfile()
        }

        binding.tvBack.setOnClickListener {
            navController.tryPopBackStack()
        }

        observeStates()
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
                    stagesAdapter.submitList(
                        it.stages
                    )
                    stagesAdapter.currentList.firstOrNull()?.apply {
                        val stageId = viewModel.getUserProfile()?.stageId
                        if(stageId == null)
                            selectStage(this)
                        else
                            stagesAdapter.selectItem(stageId)
                    }
                }
            }
        }
        lifecycleScope {
            viewModel.viewStateUpdateProfileSuccess.collectLatest {
                it?.let {
                    navController.tryNavigate(
                        StagesFragmentDirections.actionStagesFragmentToModulesFragment()
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
    private fun setupStagesAdapter() {
        stagesAdapter = StagesAdapter {
            selectStage(it)
        }

        binding.rvStages.apply {
            adapter = stagesAdapter
            addItemDecoration(
                HorizontalMarginItemDecoration(
                    dpToPx(16f, requireContext())
                )
            )
        }

//        stagesAdapter.submitList(
//            listOf(
//                Stage(
//                    id = 1, icon = "", stageName = "Stage", number = 1
//                ),
//                Stage(
//                    id = 2, icon = "", stageName = "Stage", number = 2
//                ),
//                Stage(
//                    id = 3, icon = "", stageName = "Stage", number = 3
//                ),
//                Stage(
//                    id = 4, icon = "", stageName = "Stage", number = 4
//                ),
//                Stage(
//                    id = 5, icon = "", stageName = "Stage", number = 5
//                ),
//                Stage(
//                    id = 6, icon = "", stageName = "Stage", number = 6
//                )
//            )
//        )
//        stagesAdapter.currentList.firstOrNull()?.apply {
//            selectStage(this)
//        }
    }

    private fun selectStage(it: Stage) {
        viewModel.selectStage(it.id.toInt())
    }
}