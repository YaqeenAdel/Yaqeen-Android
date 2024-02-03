package com.cancer.yaqeen.presentation.ui.main.treatment.history.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.DialogFragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.databinding.FragmentMedicationDialogBinding
import com.cancer.yaqeen.databinding.FragmentTimeBinding
import com.cancer.yaqeen.presentation.ui.main.home.article_details.ArticleDetailsFragmentArgs
import com.cancer.yaqeen.presentation.ui.main.treatment.getMedicationType
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindResourceImage
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MedicationDialogFragment : DialogFragment() {

    private var binding: FragmentMedicationDialogBinding by autoCleared()

    private lateinit var navController: NavController

    private val args: MedicationDialogFragmentArgs by navArgs()

    private val medication by lazy {
        args.medication
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMedicationDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.AppBottomDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()

        updateUI(medication)
    }

    private fun updateUI(medication: Medication) {
        with(medication){
            binding.tvTime.text = time
            binding.tvMedicationDetails.text = "$medicationName $strengthAmount $unitType"
            binding.tvDosageAmount.text = "$dosageAmount $medicationType"
            binding.tvNotes.text = notes
            getMedicationType(requireContext(), medicationType)?.run { iconResId
                bindResourceImage(binding.ivMedicationIcon, iconResId)
                bindResourceImage(binding.ivMedicationIcon2, iconResId)
            }

        }
    }

    private fun setListener() {
        binding.ivClose.setOnClickListener {
            navController.tryPopBackStack()
        }
        binding.btnEdit.setOnClickListener {
            navController.tryNavigate(
                MedicationDialogFragmentDirections.actionMedicationDialogFragmentToMedicationsFragment(medication)
            )
        }
        binding.btnTake.setOnClickListener {

        }
        binding.btnSkip.setOnClickListener {

        }
    }

}