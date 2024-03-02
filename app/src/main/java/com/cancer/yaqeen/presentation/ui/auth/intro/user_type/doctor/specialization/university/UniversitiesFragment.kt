package com.cancer.yaqeen.presentation.ui.auth.intro.user_type.doctor.specialization.university

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentUniversitiesBinding
import com.cancer.yaqeen.presentation.ui.auth.OnboardingViewModel
import com.cancer.yaqeen.presentation.util.Constants.REQUEST_UNIVERSITY_KEY
import com.cancer.yaqeen.presentation.util.Constants.UNIVERSITY_NAME_KEY
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigateUp
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class UniversitiesFragment : BottomSheetDialogFragment() {

    private var binding: FragmentUniversitiesBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var universitiesAdapter: UniversitiesAdapter

    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUniversitiesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setupUniversitiesAdapter()

        observeStates()

        binding.autoTvSearchItems.addTextChangedListener {
            universitiesAdapter.filter.filter(it.toString())
        }
    }

    private fun observeStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.viewStateUniversities.collectLatest {
                    val universityIdSelected = viewModel.getUserProfile()?.universityId
                    universitiesAdapter.setList(it, universityIdSelected)

                }
            }
        }
    }

    private fun setupUniversitiesAdapter() {
        universitiesAdapter = UniversitiesAdapter {
            viewModel.selectUniversity(it.universityID.toString())
            setFragmentResult(REQUEST_UNIVERSITY_KEY, bundleOf(UNIVERSITY_NAME_KEY to it.universityName))
            navController.tryNavigateUp()
        }

        binding.rvUniversities.apply {
            adapter = universitiesAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(15f, requireContext())
                )
            )
        }

    }
}