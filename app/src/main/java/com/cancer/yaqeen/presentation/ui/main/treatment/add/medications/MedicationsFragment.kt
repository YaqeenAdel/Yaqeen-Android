package com.cancer.yaqeen.presentation.ui.main.treatment.add.medications

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.mappers.MappingMedicationAsMedicationTrack
import com.cancer.yaqeen.data.features.home.schedule.medication.models.MedicationTrack
import com.cancer.yaqeen.databinding.FragmentMedicationsBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.getMedicationTypes
import com.cancer.yaqeen.presentation.ui.main.treatment.history.dialogs.MedicationDialogFragmentArgs
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.disable
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.enable
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedicationsFragment : BaseFragment() {

    private var binding: FragmentMedicationsBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var medicationTypesAdapter: MedicationTypesAdapter

    private val medicationsViewModel: MedicationsViewModel by activityViewModels()


    private val args: MedicationsFragmentArgs by navArgs()

    private val medication by lazy {
        args.medication
    }

    private val destinationId by lazy {
        args.destinationId
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (medication == null)
            medicationsViewModel.resetMedicationTrack()
        else
            medicationsViewModel.setMedicationTrack(
                MappingMedicationAsMedicationTrack(requireContext()).map(medication!!)
            )

        medicationsViewModel.setDestinationId(destinationId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMedicationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setupMedicationTypesAdapter()

        updateUI()

        setListener()
    }

    override fun onResume() {
        super.onResume()

        val medicationTrack = medicationsViewModel.getMedicationTrack()
        updateUI(medicationTrack)
    }

    private fun updateUI(medicationTrack: MedicationTrack?) {
        medicationTrack?.run {
            if (medicationName?.isNotEmpty() == true)
                binding.editTextCapsuleName.setText(medicationName)
            medicationType?.run {
                medicationTypesAdapter.selectItem(id)
            }
        }

        checkMedicationData()
    }

    private fun setListener(){
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnNext.setOnClickListener {
            val capsuleName = getCapsuleName()
            medicationsViewModel.selectMedicationType(medicationTypesAdapter.getItemSelected(), capsuleName.trim())
            navController.tryNavigate(
                MedicationsFragmentDirections.actionMedicationsFragmentToStrengthFragment()
            )
        }

        binding.editTextCapsuleName.addTextChangedListener {
            checkMedicationData()
        }
    }

    private fun updateUI() {
        val spannable = SpannableStringBuilder("2/3")
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary_color)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvPageNumber.text = spannable

    }
    private fun setupMedicationTypesAdapter() {
        medicationTypesAdapter = MedicationTypesAdapter {
            checkMedicationData()

            binding.textLayoutCapsuleName.hint = getString(R.string.name_of_the_medication, it.name)
        }

        binding.rvMedicationTypes.apply {
            adapter = medicationTypesAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(12f, requireContext())
                )
            )
        }

        medicationTypesAdapter.submitList(
            getMedicationTypes(requireContext())
        )
    }

    private fun checkMedicationData() {
        val capsuleName = getCapsuleName()

        val textColorId: Int
        val backgroundColorId: Int

        if(capsuleName.isNotEmpty() && medicationTypesAdapter.anyItemIsSelected()) {
            binding.btnNext.enable()
            textColorId = R.color.white
            backgroundColorId = R.color.primary_color
        }
        else {
            binding.btnNext.disable()
            textColorId = R.color.medium_gray
            backgroundColorId = R.color.light_gray
        }

        binding.btnNext.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), backgroundColorId)
        binding.btnNext.setTextColor(ContextCompat.getColorStateList(requireContext(), textColorId))
        binding.btnNext.iconTint = ContextCompat.getColorStateList(requireContext(), textColorId)

    }

    private fun getCapsuleName() = binding.editTextCapsuleName.text.toString()
}