package com.cancer.yaqeen.presentation.ui.main.treatment.add.symptoms

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentSymptomsHistoryBinding
import com.cancer.yaqeen.databinding.FragmentUniversitiesBinding
import com.cancer.yaqeen.presentation.base.BaseBottomSheetDialogFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.history.SchedulesHistoryViewModel
import com.cancer.yaqeen.presentation.ui.main.treatment.history.TreatmentHistoryFragmentDirections
import com.cancer.yaqeen.presentation.ui.main.treatment.history.adapters.SymptomsAdapter
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryNavigateUp
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class SymptomsHistoryFragment : BaseBottomSheetDialogFragment(showBottomMenu = true) {

    private var binding: FragmentSymptomsHistoryBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var symptomsAdapter: SymptomsAdapter

    private val viewModel: SchedulesHistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSymptomsHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setupSymptomsAdapter()
        observeStates()

        viewModel.getSymptoms()
    }

    private fun setupSymptomsAdapter() {
        symptomsAdapter = SymptomsAdapter(
            itemClickable = true,
            onItemClick = {
                setFragmentResult(Constants.REQUEST_SYMPTOM_KEY, bundleOf(Constants.SYMPTOM_KEY to it))
                navController.tryNavigateUp()
            }
        )
        binding.rvSymptomsHistory.apply {
            adapter = symptomsAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(24f, requireContext())
                )
            )
        }
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
            viewModel.viewStateSymptoms.collect { symptoms ->
                symptomsAdapter.setList(symptoms)
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

}