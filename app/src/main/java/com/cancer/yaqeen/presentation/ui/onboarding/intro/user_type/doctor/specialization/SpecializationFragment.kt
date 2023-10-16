package com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.doctor.specialization

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentSelectUserTypeBinding
import com.cancer.yaqeen.databinding.FragmentSpecializationBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.onboarding.OnboardingViewModel
import com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.SelectUserTypeFragmentDirections
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.selectItem
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SpecializationFragment : BaseFragment() {

    private var binding: FragmentSpecializationBinding by autoCleared()

    private lateinit var navController: NavController

    private val viewModel: OnboardingViewModel by activityViewModels()

    private lateinit var universityAutoCompleteAdapter: UniversityAutoCompleteAdapter
    private lateinit var degreeAutoCompleteAdapter: UniversityAutoCompleteAdapter
    private lateinit var medicalFieldAutoCompleteAdapter: UniversityAutoCompleteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSpecializationBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.tvNext.setOnClickListener {
            navController.tryNavigate(
                SpecializationFragmentDirections.actionSpecializationFragmentToModulesFragment()
            )
        }

        binding.tvBack.setOnClickListener {
            navController.tryPopBackStack()
        }


        setupDropDownSpecialization()
        setupDropDownDegree()
        setupDropDownMedicalField()

        observeStates()
    }
    private fun observeStates() {
        lifecycleScope {
            viewModel.viewStateResources.collectLatest {
//                stagesAdapter.submitList(
//                    it.stages
//                )
            }
        }
    }

    private fun setupDropDownSpecialization() {
        universityAutoCompleteAdapter = UniversityAutoCompleteAdapter(
            requireContext()
        )

        binding.autoTvUniversity.setAdapter(universityAutoCompleteAdapter)

        universityAutoCompleteAdapter.setList(
            listOf(
                "Ain Shams", "Cairo", "Zagazig", "Alexandria", "Tanta", "Assuit", "Aswan"
            )
        )

        binding.autoTvUniversity.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                viewModel.selectUniversity(universityAutoCompleteAdapter.getItem(position))
            }

//        binding.autoTvUniversity.selectItem(2, universityAutoCompleteAdapter.getItem(2))
    }

    private fun setupDropDownDegree() {
        degreeAutoCompleteAdapter = UniversityAutoCompleteAdapter(
            requireContext()
        )

        binding.autoTvDegree.setAdapter(degreeAutoCompleteAdapter)

        degreeAutoCompleteAdapter.setList(
            listOf(
                "BSC", "Master", "PHD"
            )
        )

        binding.autoTvDegree.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                viewModel.selectDegree(degreeAutoCompleteAdapter.getItem(position))
            }

//        binding.autoTvDegree.selectItem(1, degreeAutoCompleteAdapter.getItem(1))
    }

    private fun setupDropDownMedicalField() {
        medicalFieldAutoCompleteAdapter = UniversityAutoCompleteAdapter(
            requireContext()
        )

        binding.autoTvMedicalField.setAdapter(medicalFieldAutoCompleteAdapter)

        medicalFieldAutoCompleteAdapter.setList(
            listOf(
                "Field 1", "Field 2", "Field 3"
            )
        )

        binding.autoTvMedicalField.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                viewModel.selectMedicalField(medicalFieldAutoCompleteAdapter.getItem(position))
            }

//        binding.autoTvMedicalField.selectItem(0, medicalFieldAutoCompleteAdapter.getItem(0))
    }

}