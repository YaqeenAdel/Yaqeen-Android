package com.cancer.yaqeen.presentation.ui.main.treatment.add.symptoms.details.choose_time

import android.net.Uri
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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.MedicationTrack
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomTrack
import com.cancer.yaqeen.databinding.FragmentChooseTimeBinding
import com.cancer.yaqeen.databinding.FragmentChooseTimeSymptomBinding
import com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.strength.choose_time.ChooseTimeFragmentDirections
import com.cancer.yaqeen.presentation.ui.main.treatment.add.symptoms.SymptomsViewModel
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate
import com.cancer.yaqeen.presentation.util.disable
import com.cancer.yaqeen.presentation.util.enable
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChooseTimeSymptomFragment : Fragment() {

    private var binding: FragmentChooseTimeSymptomBinding by autoCleared()

    private lateinit var navController: NavController

    private val symptomsViewModel: SymptomsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChooseTimeSymptomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(Constants.REQUEST_DATE_KEY) { requestKey, bundle ->
            if (requestKey == Constants.REQUEST_DATE_KEY) {
                val date = bundle.getLong(Constants.DATE_SELECTED_KEY)
                val startDate = convertMilliSecondsToDate(date)
                binding.editTextStartFrom.setText(startDate)
                symptomsViewModel.selectStartDate(startDate)

                checkPeriodTimeData()
            }
        }

        setFragmentResultListener(Constants.REQUEST_REMINDER_TIME_KEY) { requestKey, bundle ->
            if (requestKey == Constants.REQUEST_REMINDER_TIME_KEY) {
                val reminderTime: ReminderTime? = bundle.getParcelable(Constants.REMINDER_TIME_KEY)
                reminderTime?.run {
                    val timing = if (isAM) getString(R.string.am) else getString(R.string.pm)
                    binding.editTextTime.setText("$text $timing")
                }
                val time = if (reminderTime != null) {
                    val timing = if (reminderTime.isAM) getString(R.string.am) else getString(R.string.pm)
                    "${reminderTime.text} $timing"
                }
                else null

                binding.editTextTime.setText(time ?: "")
                symptomsViewModel.selectReminderTime(time)

                checkPeriodTimeData()
            }
        }

        navController = findNavController()

        updateUI()

        setListener()
    }

    override fun onResume() {
        super.onResume()

        val symptomTrack = symptomsViewModel.getSymptomTrack()
        updateUI(symptomTrack)
    }

    private fun updateUI(symptomTrack: SymptomTrack?) {
        symptomTrack?.run {
            if (startDate != null)
                binding.editTextStartFrom.setText(startDate!!)
            if (reminderTime?.isNotEmpty() == true) {
                binding.editTextTime.setText("$reminderTime")
            }
            if (details?.isNotEmpty() == true)
                binding.tvSymptomDetails.text = details
            val isReminder = doctorName?.isNotEmpty() == true
            binding.textLayoutDoctorName.changeVisibility(show = isReminder, isGone = true)
            if (isReminder)
                binding.editTextDoctorName.setText(doctorName)

            binding.checkboxMedication.isChecked = isReminder

            imageUri?.let {
                updateUI(imageUri)
            }
            imageDownloadUrl?.let {
                updateUI(imageDownloadUrl)
            }
        }

        checkPeriodTimeData()
    }

    private fun updateUI(url: String?) {
        bindImage(binding.ivSymptom, url)
    }

    private fun updateUI(uri: Uri?) {
        binding.ivSymptom.setImageURI(uri)
    }

    private fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
        binding.btnNext.setOnClickListener {
            val isChecked = binding.checkboxMedication.isChecked
            val doctorName = if (isChecked)
                binding.editTextDoctorName.text.toString()
            else
                ""
            symptomsViewModel.setDoctorName(doctorName)
            navController.tryNavigate(
                ChooseTimeSymptomFragmentDirections.actionChooseTimeSymptomFragmentToSymptomConfirmationFragment()
            )
        }
        binding.editTextStartFrom.setOnClickListener {
            navController.tryNavigate(
                R.id.calendarFragment
            )
        }
        binding.editTextTime.setOnClickListener {
            navController.tryNavigate(
                R.id.timeFragment
            )
        }
        binding.layoutReminder.setOnClickListener {
            val isChecked = binding.checkboxMedication.isChecked
            binding.checkboxMedication.isChecked = !isChecked

            binding.textLayoutDoctorName.changeVisibility(show = !isChecked, isGone = true)

            checkPeriodTimeData()
        }

        binding.editTextDoctorName.addTextChangedListener {
            checkPeriodTimeData()
        }
    }

    private fun updateUI() {
        val spannable = SpannableStringBuilder("3/3")
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
    private fun checkPeriodTimeData() {
        val date = binding.editTextStartFrom.text.toString()
        val time = binding.editTextTime.text.toString()
        val isChecked = binding.checkboxMedication.isChecked
        val doctorName = binding.editTextDoctorName.text.toString()

        val textColorId: Int
        val backgroundColorId: Int

        val isEnabled = if (isChecked){
            doctorName.isNotEmpty()
        }else {
            true
        }

        if (date.isNotEmpty() && time.isNotEmpty() && isEnabled) {
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