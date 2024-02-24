package com.cancer.yaqeen.presentation.ui.main.treatment.add.symptoms

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
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.mappers.MappingMedicationAsMedicationTrack
import com.cancer.yaqeen.data.features.home.schedule.medication.models.MedicationTrack
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingSymptomAsSymptomTrack
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomTrack
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomType
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentSymptomsTypesBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.MedicationsFragmentArgs
import com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.MedicationsViewModel
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.disable
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.enable
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SymptomsTypesFragment : BaseFragment() {

    private var binding: FragmentSymptomsTypesBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var symptomsTypesAdapter: SymptomsTypesAdapter

    private val symptomsViewModel: SymptomsViewModel by activityViewModels()


    private val args: SymptomsTypesFragmentArgs by navArgs()

    private val symptom by lazy {
        args.symptom
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("TAG", "onCreatesavedInstanceState: $symptom")
        if (symptom == null)
            symptomsViewModel.resetSymptomTrack()
        else
            symptomsViewModel.setSymptomTrack(
                MappingSymptomAsSymptomTrack().map(symptom!!)
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSymptomsTypesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()
        updateUI()

        setupSymptomsTypesAdapter()

        observeStates()

        symptomsViewModel.getSymptomsTypes()
    }

    override fun onResume() {
        super.onResume()

        val symptomTrack = symptomsViewModel.getSymptomTrack()
        updateUI(symptomTrack)
    }

    private fun updateUI(symptomTrack: SymptomTrack?) {
        symptomTrack?.run {
            Log.d("TAG", "updateUI: symptomTrack $symptomTypes")
//            if (symptomTypes == null)
//                symptomsTypesAdapter.setList(null)
//            else
                symptomsTypesAdapter.selectItems(symptomTypes)
        }

        checkSymptomData()
    }

    private fun setupSymptomsTypesAdapter() {
        symptomsTypesAdapter = SymptomsTypesAdapter {
            checkSymptomData(it.selected)
        }

        binding.rvSymptomsTypes.apply {
            adapter = symptomsTypesAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(8f, requireContext())
                )
            )
        }

    }

    private fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnNext.setOnClickListener {
            symptomsViewModel.selectSymptomTypes(symptomsTypesAdapter.getItemsSelected())
            navController.tryNavigate(
                SymptomsTypesFragmentDirections.actionSymptomsTypesFragmentToSymptomsDetailsFragment()
            )
        }
    }

    private fun updateUI() {
        val spannable = SpannableStringBuilder("1/3")
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary_color)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvPageNumber.text = spannable

    }
    private fun observeStates() {
        lifecycleScope {
            symptomsViewModel.viewStateLoading.collectLatest {
                onLoading(it)
            }
        }
        lifecycleScope {
            symptomsViewModel.viewStateError.collectLatest {
                handleResponseError(it)
            }
        }

        lifecycleScope {
            symptomsViewModel.viewStateSymptomsTypes.collect { symptomsTypes ->
                Log.d("TAG", "updateUI: symptomTrack2 $symptomsTypes")
                symptomsTypesAdapter.setList(symptomsTypes)
                checkSymptomData()
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

    private fun checkSymptomData(selected: Boolean = false) {
        var isSelected = selected
        if (!selected)
            isSelected = symptomsTypesAdapter.anyItemIsSelected()


        val textColorId: Int
        val backgroundColorId: Int

        if (isSelected) {
            binding.btnNext.enable()
            textColorId = R.color.white
            backgroundColorId = R.color.primary_color
        } else {
            binding.btnNext.disable()
            textColorId = R.color.medium_gray
            backgroundColorId = R.color.light_gray
        }

        binding.btnNext.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), backgroundColorId)
        binding.btnNext.setTextColor(ContextCompat.getColorStateList(requireContext(), textColorId))
        binding.btnNext.iconTint = ContextCompat.getColorStateList(requireContext(), textColorId)
    }
}