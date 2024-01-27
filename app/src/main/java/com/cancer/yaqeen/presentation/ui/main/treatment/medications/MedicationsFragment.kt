package com.cancer.yaqeen.presentation.ui.main.treatment.medications

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.models.MedicationType
import com.cancer.yaqeen.databinding.FragmentMedicationsBinding
import com.cancer.yaqeen.databinding.FragmentStagesBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.auth.intro.user_type.patient.stages.StagesAdapter
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.disable
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.enable
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedicationsFragment : BaseFragment() {

    private var binding: FragmentMedicationsBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var medicationTypesAdapter: MedicationTypesAdapter
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
    private fun setListener(){
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnNext.setOnClickListener {
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
            listOf(
                MedicationType(
                    id = 1, name = "Capsule", url = ""
                ),
                MedicationType(
                    id = 2, name = "Pills", url = ""
                ),
                MedicationType(
                    id = 3, name = "Liquid", url = ""
                ),
                MedicationType(
                    id = 4, name = "Injection", url = ""
                ),
                MedicationType(
                    id = 5, name = "Injection 2", url = ""
                ),
                MedicationType(
                    id = 6, name = "Injection 3", url = ""
                ),
                MedicationType(
                    id = 7, name = "Injection 4", url = ""
                ),
                MedicationType(
                    id = 8, name = "Injection 5", url = ""
                )
            )
        )
    }

    private fun checkMedicationData() {
        val capsuleName = binding.editTextCapsuleName.text.toString()

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
}