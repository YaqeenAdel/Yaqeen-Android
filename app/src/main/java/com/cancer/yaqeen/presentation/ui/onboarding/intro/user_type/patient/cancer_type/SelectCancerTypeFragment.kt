package com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.patient.cancer_type

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.auth.models.UserType
import com.cancer.yaqeen.data.features.onboarding.models.CancerType
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentSelectCancerTypeBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.onboarding.OnboardingViewModel
import com.cancer.yaqeen.presentation.ui.onboarding.terms_condition.TermsAndConditionFragmentDirections
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SelectCancerTypeFragment : BaseFragment() {

    private var binding: FragmentSelectCancerTypeBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var cancerTypesAdapter: CancerTypesAdapter


    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSelectCancerTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setupCancerTypesAdapter()

        updateUI()

        setListener()

//        observeStates()
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

        binding.autoTvSearchItems.addTextChangedListener {
            cancerTypesAdapter.filter.filter(it.toString())
        }
    }
    private fun updateUI() {
        val spannable = SpannableStringBuilder("2/4")
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
                    cancerTypesAdapter.setList(
                        it.cancerTypes
                    )
                    cancerTypesAdapter.currentList.firstOrNull()?.apply {
                        val cancerTypeId = viewModel.getUserProfile()?.cancerTypeId
                        if(cancerTypeId == null)
                            selectCancerType(this)
                        else
                            cancerTypesAdapter.selectItem(cancerTypeId)
                    }
                }

            }
        }
        lifecycleScope {
            viewModel.viewStateUpdateProfileSuccess.collectLatest {
                it?.let {
                    navController.tryNavigate(
                        SelectCancerTypeFragmentDirections.actionSelectCancerTypeFragmentToStagesFragment()
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

    private fun setupCancerTypesAdapter() {
        cancerTypesAdapter = CancerTypesAdapter {
            selectCancerType(it)
        }

        binding.rvCancerTypes.apply {
            adapter = cancerTypesAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(15f, requireContext())
                )
            )
        }

        cancerTypesAdapter.setList(
            listOf(
                CancerType(
                    id = 1, icon = "", typeName = "Selection item"
                ),
                CancerType(
                    id = 2, icon = "", typeName = "Selection item"
                ),
                CancerType(
                    id = 3, icon = "", typeName = "Selection item"
                ),
                CancerType(
                    id = 4, icon = "", typeName = "Selection item"
                ),
                CancerType(
                    id = 5, icon = "", typeName = "Other "
                ),
            )
        )
        cancerTypesAdapter.currentList.firstOrNull()?.apply {
            selectCancerType(this)
        }

    }

    private fun selectCancerType(it: CancerType) {
        viewModel.selectCancerType(it.id.toInt())
    }
}