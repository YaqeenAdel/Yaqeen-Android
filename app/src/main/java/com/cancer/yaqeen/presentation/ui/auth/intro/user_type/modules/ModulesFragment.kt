package com.cancer.yaqeen.presentation.ui.auth.intro.user_type.modules

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
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
import com.cancer.yaqeen.data.features.auth.models.UserType
import com.cancer.yaqeen.data.features.onboarding.models.Module
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentModulesBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.auth.OnboardingViewModel
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.CenterGridMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

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

    override fun onResume() {
        super.onResume()

        binding.autoTvSearchItems.setText("")
    }

    private fun setListener(){
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnFinish.setOnClickListener {
            viewModel.updateUserProfile(true)
        }

        binding.autoTvSearchItems.addTextChangedListener {
            modulesAdapter.filter.filter(it.toString())
        }
    }
    private fun updateUI() {
        val spannable = SpannableStringBuilder("3/3")
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
                    modulesAdapter.setList(modules)
                    val interestModuleIds = viewModel.getUserProfile()?.interestModuleIds
                    Log.d("TAG", "observeStates: $interestModuleIds")
                    if(!interestModuleIds.isNullOrEmpty()) {
                        binding.btnFinish.changeVisibility(show = true)
                        modulesAdapter.selectItems(interestModuleIds)
                    }

                }

            }
        }
        lifecycleScope {
            viewModel.viewStateUpdateProfileSuccess.collectLatest {
                it?.let {
                    navController.tryNavigate(
                        ModulesFragmentDirections.actionModulesFragmentToQuoteFragment()
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
//            viewModel.updateUserProfile()

            if (it.selected){
                binding.btnFinish.changeVisibility(show = true)
            }else {
                binding.btnFinish.changeVisibility(show = !modulesAdapter.allItemsUnSelected(), isGone = false)
            }
        }

        binding.rvModules.apply {
            adapter = modulesAdapter
            addItemDecoration(
                CenterGridMarginItemDecoration(
                    2,
                    dpToPx(1f, requireContext())
                )
            )
        }

    }

    private fun selectInterestModule(module: Module) {
        viewModel.selectInterestModule(module)
    }
}