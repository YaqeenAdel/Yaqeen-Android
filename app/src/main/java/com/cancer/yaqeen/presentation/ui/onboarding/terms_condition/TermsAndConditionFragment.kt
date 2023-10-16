package com.cancer.yaqeen.presentation.ui.onboarding.terms_condition

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.onboarding.models.Module
import com.cancer.yaqeen.data.features.onboarding.models.TermsAndCondition
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentIntroBinding
import com.cancer.yaqeen.databinding.FragmentTermsAndConditionBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.onboarding.OnboardingViewModel
import com.cancer.yaqeen.presentation.ui.onboarding.intro.IntroFragmentDirections
import com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.modules.ModulesAdapter
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.GridMarginItemDecoration
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TermsAndConditionFragment : BaseFragment() {

    private var binding: FragmentTermsAndConditionBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var termsAndConditionAdapter: TermsAndConditionAdapter

    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTermsAndConditionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setupTermsAndConditionAdapter()

        binding.btnAccept.setOnClickListener {
            viewModel.updateUserProfile()
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
            viewModel.viewStateError.collectLatest {
                handleResponseError(it)
            }
        }
        lifecycleScope {
            viewModel.viewStateUpdateProfileSuccess.collectLatest {
                it?.let {
                    navController.tryNavigate(
                        TermsAndConditionFragmentDirections.actionTermsAndConditionFragmentToActivationFragment()
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

    private fun setupTermsAndConditionAdapter() {
        termsAndConditionAdapter = TermsAndConditionAdapter()

        binding.rvTermsCondition.apply {
            adapter = termsAndConditionAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(16f, requireContext())
                )
            )
        }

        val fakeDetails = "Lorem ipsum dolor sit amet consectetur. Magna diam dolor bibendum suspendisse sit pellentesque. Sapien mauris viverra lorem iaculis. Sit sollicitudin odio fames sit facilisi aliquam tortor tincidunt. Mauris ut posuere convallis porttitor. Ultrices elementum lacus convallis in pellentesque dictum quam eros sit. Nisi massa mauris arcu bibendum orci etiam egestas ac. Tempor pulvinar quis sit blandit at urna."
        termsAndConditionAdapter.submitList(
            listOf(
                TermsAndCondition(
                    id = 1, titleName = "#title name", details = fakeDetails
                ),
                TermsAndCondition(
                    id = 2, titleName = "#title name", details = fakeDetails
                ),
                TermsAndCondition(
                    id = 3, titleName = "#title name", details = fakeDetails
                ),
                TermsAndCondition(
                    id = 4, titleName = "#title name", details = fakeDetails
                ),
                TermsAndCondition(
                    id = 5, titleName = "#title name", details = fakeDetails
                ),
                TermsAndCondition(
                    id = 6, titleName = "#title name", details = fakeDetails
                ),
                TermsAndCondition(
                    id = 7, titleName = "#title name", details = fakeDetails
                ),
                TermsAndCondition(
                    id = 8, titleName = "#title name", details = fakeDetails
                ),
                TermsAndCondition(
                    id = 9, titleName = "#title name", details = fakeDetails
                ),
                TermsAndCondition(
                    id = 10, titleName = "#title name", details = fakeDetails
                )
            )
        )
    }
}