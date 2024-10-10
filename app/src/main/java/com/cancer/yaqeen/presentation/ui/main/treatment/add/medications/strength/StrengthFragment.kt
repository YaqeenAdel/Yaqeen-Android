package com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.strength

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
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.MedicationTrack
import com.cancer.yaqeen.data.utils.toJson
import com.cancer.yaqeen.databinding.FragmentStrengthBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.MedicationsViewModel
import com.cancer.yaqeen.presentation.ui.main.treatment.getUnitTypes
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.disable
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.enable
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.CAPSULE_NAME
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.MEDICATION_TYPE
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.STRENGTH_AMOUNT
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.UNIT_TYPE
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvent
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.SELECT_MEDICATION_TYPE
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.SELECT_STRENGTH_AMOUNT
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StrengthFragment : BaseFragment() {

    private var binding: FragmentStrengthBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var unitTypesAdapter: UnitTypesAdapter

    private val medicationsViewModel: MedicationsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStrengthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setupUnitTypesAdapter()

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
            if (strengthAmount?.isNotEmpty() == true)
                binding.editTextCount.setText(strengthAmount)
            binding.toolbar.title = medicationName
            binding.tvMedicationType.text = medicationType?.name ?: ""
            unitType?.run {
                unitTypesAdapter.selectItem(id)
            }

            binding.tvStrength2.text = getString(R.string.strength_question, medicationType?.name ?: "")
        }

        checkStrengthData()
    }

    private fun setListener(){
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnNext.setOnClickListener {
            val strengthAmount = getStrengthAmount()
            val unitType = unitTypesAdapter.getItemSelected()
            medicationsViewModel.selectUnitType(unitType, strengthAmount.trim())
            medicationsViewModel.logEvent(
                GoogleAnalyticsEvent(
                    eventName = SELECT_STRENGTH_AMOUNT,
                    eventParams = arrayOf(
                        STRENGTH_AMOUNT to strengthAmount,
                        UNIT_TYPE to unitType.toJson(),
                    )
                )
            )
            navController.tryNavigate(
                StrengthFragmentDirections.actionStrengthFragmentToChooseTimeFragment()
            )
        }

        binding.editTextCount.addTextChangedListener {
            checkStrengthData()
        }
    }

    private fun updateUI() {
        val spannable = SpannableStringBuilder("2/3")
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary_color)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvPageNumber.text = spannable

    }
    private fun setupUnitTypesAdapter() {
        unitTypesAdapter = UnitTypesAdapter {
            checkStrengthData()
        }

        binding.rvUnitTypes.apply {
            adapter = unitTypesAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(12f, requireContext())
                )
            )
        }

        unitTypesAdapter.submitList(
            getUnitTypes(requireContext())
        )
    }

    private fun checkStrengthData() {
        val medicationAmount = getStrengthAmount()

        val textColorId: Int
        val backgroundColorId: Int

        if(medicationAmount.isNotEmpty() && medicationAmount != "0" && unitTypesAdapter.anyItemIsSelected()) {
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

    private fun getStrengthAmount() = binding.editTextCount.text.toString()
}