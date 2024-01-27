package com.cancer.yaqeen.presentation.ui.main.treatment.medications.strength

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
import com.cancer.yaqeen.data.features.home.models.UnitType
import com.cancer.yaqeen.databinding.FragmentMedicationsBinding
import com.cancer.yaqeen.databinding.FragmentStrengthBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.medications.MedicationTypesAdapter
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.disable
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.enable
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StrengthFragment : BaseFragment() {

    private var binding: FragmentStrengthBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var unitTypesAdapter: UnitTypesAdapter

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

        binding.toolbar.title = "CINNARIZINE"
        binding.tvMedicationType.text = "Capsule"
    }
    private fun setListener(){
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnNext.setOnClickListener {
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
            listOf(
                UnitType(
                    id = 1, name = "Mg"
                ),
                UnitType(
                    id = 2, name = "Mcg"
                ),
                UnitType(
                    id = 3, name = "Ml"
                ),
                UnitType(
                    id = 4, name = "%"
                ),
                UnitType(
                    id = 5, name = "%%"
                ),
                UnitType(
                    id = 6, name = "%%%"
                ),
                UnitType(
                    id = 7, name = "%%%%"
                ),
                UnitType(
                    id = 8, name = "%%%%%"
                )
            )
        )
    }

    private fun checkStrengthData() {
        val count = binding.editTextCount.text.toString()

        val textColorId: Int
        val backgroundColorId: Int

        if(count.isNotEmpty() && unitTypesAdapter.anyItemIsSelected()) {
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