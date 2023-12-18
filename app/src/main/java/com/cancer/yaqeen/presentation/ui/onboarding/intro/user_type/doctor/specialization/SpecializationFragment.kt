package com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.doctor.specialization

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentSelectUserTypeBinding
import com.cancer.yaqeen.databinding.FragmentSpecializationBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.onboarding.OnboardingViewModel
import com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.SelectUserTypeFragmentDirections
import com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.modules.ModulesFragmentDirections
import com.cancer.yaqeen.presentation.util.Constants.REQUEST_UNIVERSITY_KEY
import com.cancer.yaqeen.presentation.util.Constants.UNIVERSITY_NAME_KEY
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.disable
import com.cancer.yaqeen.presentation.util.enable
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Use the Kotlin extension in the fragment-ktx artifact.
        setFragmentResultListener(REQUEST_UNIVERSITY_KEY) { requestKey, bundle ->
            if(requestKey == REQUEST_UNIVERSITY_KEY) {
                val universityName = bundle.getString(UNIVERSITY_NAME_KEY)
                binding.autoTvUniversity.setText(universityName)
            }
        }
    }

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

        setListener()

//        setupDropDownSpecialization()
        setupDropDownDegree()
        setupDropDownMedicalField()

        observeStates()

        getUniversities()

    }

    private fun getUniversities(){
        viewModel.getUniversities("eg", "432237125")
    }

    private fun setListener() {

        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnNext.setOnClickListener {
            viewModel.updateUserProfile()
        }

        binding.btnPrevious.setOnClickListener {
            navController.tryPopBackStack()
        }

        binding.autoTvUniversity.setOnClickListener {
            navController.tryNavigate(
                SpecializationFragmentDirections.actionSpecializationFragmentToUniversitiesFragment()
            )
        }
        binding.autoTvUniversity.addTextChangedListener {
            checkDataInput()
        }
        binding.autoTvDegree.addTextChangedListener {
            checkDataInput()
        }
        binding.autoTvMedicalField.addTextChangedListener {
            checkDataInput()
        }
    }

    private fun checkDataInput() {
        val university = binding.autoTvUniversity.text.toString()
        val degree = binding.autoTvDegree.text.toString()
        val medicalField = binding.autoTvMedicalField.text.toString()

        if(university.isNotEmpty() && degree.isNotEmpty() && medicalField.isNotEmpty())
            binding.btnNext.enable()
        else
            binding.btnNext.disable()
    }

    private fun observeStates() {
        lifecycleScope {
            viewModel.viewStateLoading.collectLatest {
                onLoading(it)
            }
        }
        lifecycleScope {
            viewModel.viewStateResources.collectLatest {
//                stagesAdapter.submitList(
//                    it.stages
//                )
            }
        }
        lifecycleScope {
            viewModel.viewStateUniversities.collectLatest { universities ->
                val universityId = viewModel.getUserProfile()?.universityId
                if(universityId != null){
                    universities.firstOrNull{ university ->
                        university.universityID.toString() == universityId
                    }?.run {
                        binding.autoTvUniversity.setText(universityName)
                    }
                }
            }
        }
        lifecycleScope {
            viewModel.viewStateUpdateProfileSuccess.collectLatest {
                it?.let {
                    navController.tryNavigate(
                        SpecializationFragmentDirections.actionSpecializationFragmentToModulesFragment()
                    )
                }
            }
        }
    }

//    private fun setupDropDownSpecialization() {
//        universityAutoCompleteAdapter = UniversityAutoCompleteAdapter(
//            requireContext()
//        )
//
//        binding.autoTvUniversity.setAdapter(universityAutoCompleteAdapter)
//
//        universityAutoCompleteAdapter.setList(
//            listOf(
//                "Ain Shams", "Cairo", "Zagazig", "Alexandria", "Tanta", "Assuit", "Aswan"
//            )
//        )
//
//        binding.autoTvUniversity.onItemClickListener =
//            AdapterView.OnItemClickListener { parent, view, position, id ->
//                Log.d("TAG", "setupDropDownSpecialization: $position")
//                viewModel.selectUniversity(universityAutoCompleteAdapter.getItem(position))
//            }
//
////        binding.autoTvUniversity.selectItem(2, universityAutoCompleteAdapter.getItem(2))
//    }

    private fun setupDropDownDegree() {
        degreeAutoCompleteAdapter = UniversityAutoCompleteAdapter(requireContext()){
            viewModel.selectDegree(it)
            binding.autoTvDegree.setText(it)
        }

        binding.autoTvDegree.setAdapter(degreeAutoCompleteAdapter)

        degreeAutoCompleteAdapter.setList(
            listOf(
                "BSC", "Master", "PHD"
            )
        )


//        binding.autoTvDegree.selectItem(1, degreeAutoCompleteAdapter.getItem(1))
    }

    private fun setupDropDownMedicalField() {
        medicalFieldAutoCompleteAdapter = UniversityAutoCompleteAdapter(requireContext()){
            viewModel.selectMedicalField(it)
            binding.autoTvMedicalField.setText(it)
        }

        binding.autoTvMedicalField.setAdapter(medicalFieldAutoCompleteAdapter)

        medicalFieldAutoCompleteAdapter.setList(
            listOf(
                "Field 1", "Field 2", "Field 3"
            )
        )

//        binding.autoTvMedicalField.selectItem(0, medicalFieldAutoCompleteAdapter.getItem(0))
    }

}