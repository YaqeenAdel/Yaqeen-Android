package com.cancer.yaqeen.presentation.ui.main.treatment.medications.strength.choose_time.confirmation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentChooseTimeBinding
import com.cancer.yaqeen.databinding.FragmentMedicationConfirmationBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MedicationConfirmationFragment : BottomSheetDialogFragment() {

    private var binding: FragmentMedicationConfirmationBinding by autoCleared()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMedicationConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    private fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnConfirm.setOnClickListener {
            navController.tryPopBackStack(
                R.id.homeFragment,
                false
            )
        }
    }

}