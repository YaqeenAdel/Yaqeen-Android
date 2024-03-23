package com.cancer.yaqeen.presentation.ui.main.treatment.add.medical_reminder

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
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models.MedicalReminderTrack
import com.cancer.yaqeen.data.features.home.schedule.medication.mappers.MappingMedicationAsMedicationTrack
import com.cancer.yaqeen.databinding.FragmentMedicalReminderInfoBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.disable
import com.cancer.yaqeen.presentation.util.enable
import com.cancer.yaqeen.presentation.util.isValidPhone
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MedicalReminderInfoFragment : BaseFragment() {

    private var binding: FragmentMedicalReminderInfoBinding by autoCleared()

    private lateinit var navController: NavController

    private val medicalReminderViewModel: MedicalReminderViewModel by activityViewModels()

//    private val medication by lazy {
//        args.medication
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (medicalReminder == null)
            medicalReminderViewModel.resetMedicalReminderTrack()
//        else
//            medicalReminderViewModel.setMedicalReminderTrack(
//                MappingMedicationAsMedicationTrack(requireContext()).map(medication!!)
//            )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMedicalReminderInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        navController = findNavController()

        updateUI()

        setListener()
    }

    override fun onResume() {
        super.onResume()

        val medicalReminderTrack = medicalReminderViewModel.getMedicalReminderTrack()
        updateUI(medicalReminderTrack)
    }

    private fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnNext.setOnClickListener {
            saveMedicalReminderInfo()

            navController.tryNavigate(
                MedicalReminderInfoFragmentDirections.actionMedicalReminderInfoFragmentToChooseTimeMedicalReminderFragment()
            )
        }

        binding.editTextDoctorName.addTextChangedListener {
            checkMedicalReminderData()
        }

        binding.editTextLocation.addTextChangedListener {
            checkMedicalReminderData()
        }

        binding.editTextPhoneNumber.addTextChangedListener {
            checkMedicalReminderData()
        }
    }

    private fun saveMedicalReminderInfo() {
        val doctorName = binding.editTextDoctorName.text.toString()
        val location = binding.editTextLocation.text.toString()
        val phoneNumber = binding.editTextPhoneNumber.text.toString()
        medicalReminderViewModel.setMedicalReminderInfo(
            doctorName = doctorName,
            location = location,
            phoneNumber = phoneNumber
        )
    }

    private fun updateUI(medicalReminderTrack: MedicalReminderTrack?){
        medicalReminderTrack?.run {
            if (doctorName?.isNotEmpty() == true) {
                binding.editTextDoctorName.setText(doctorName)
            }
            if (location?.isNotEmpty() == true) {
                binding.editTextLocation.setText(location)
            }
            if (phoneNumber?.isNotEmpty() == true) {
                binding.editTextPhoneNumber.setText(phoneNumber)
            }

        }
        checkMedicalReminderData()
    }

    private fun updateUI() {
        val spannable = SpannableStringBuilder("2/3")
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primary_color
                )
            ), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvPageNumber.text = spannable

    }

    private fun checkMedicalReminderData() {
        val doctorName = binding.editTextDoctorName.text.toString()
        val location = binding.editTextLocation.text.toString()
        val phoneNumber = binding.editTextPhoneNumber.text.toString()

        val textColorId: Int
        val backgroundColorId: Int

        if (doctorName.isNotEmpty() && location.isNotEmpty() && phoneNumber.isNotEmpty() && phoneNumber.isValidPhone()) {
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