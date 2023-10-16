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
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SpecializationFragment : BaseFragment() {

    private var binding: FragmentSpecializationBinding by autoCleared()

    private lateinit var navController: NavController

    private val viewModel: OnboardingViewModel by activityViewModels()

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
        val specializationList = arrayOf("Ain Shams", "Cairo", "Zagazig", "Alexandria", "Tanta", "Assuit", "Aswan")

        val adapter = ArrayAdapter<String>(requireContext(), R.layout.item_dropdown_spinner, specializationList)
        binding.autoTvUniversity.setAdapter(adapter)

        binding.autoTvUniversity.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                viewModel.selectUniversity(specializationList[position])
            }
    }

    private fun setupDropDownDegree() {
        val specializationList = arrayOf("BSC", "Master", "PHD")

        val adapter = ArrayAdapter<String>(requireContext(), R.layout.item_dropdown_spinner, specializationList)
        binding.autoTvDegree.setAdapter(adapter)

        binding.autoTvDegree.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                viewModel.selectDegree(specializationList[position])
            }
    }

    private fun setupDropDownMedicalField() {
        val specializationList = arrayOf("Field 1", "Field 2", "Field 3")

        val adapter = ArrayAdapter<String>(requireContext(), R.layout.item_dropdown_spinner, specializationList)
        binding.autoTvMedicalField.setAdapter(adapter)

        binding.autoTvMedicalField.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                viewModel.selectMedicalField(specializationList[position])
            }
    }

}