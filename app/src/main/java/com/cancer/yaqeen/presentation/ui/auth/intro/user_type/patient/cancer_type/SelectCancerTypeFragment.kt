package com.cancer.yaqeen.presentation.ui.auth.intro.user_type.patient.cancer_type

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.onboarding.models.CancerType
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentSelectCancerTypeBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.auth.OnboardingViewModel
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

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

        observeStates()
    }

    override fun onResume() {
        super.onResume()

        binding.autoTvSearchItems.setText("")
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
        val spannable = SpannableStringBuilder("1/3")
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
            navController.tryNavigate(
                SelectCancerTypeFragmentDirections.actionSelectCancerTypeFragmentToStagesFragment()
            )
//            viewModel.updateUserProfile()
        }

        binding.rvCancerTypes.apply {
            adapter = cancerTypesAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(15f, requireContext())
                )
            )
        }

    }

    private fun selectCancerType(it: CancerType) {
        viewModel.selectCancerType(it.id.toInt())
    }
}