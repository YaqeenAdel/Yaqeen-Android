package com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.strength.choose_time.select_time

import android.content.res.ColorStateList
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
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.MedicationTrack
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime
import com.cancer.yaqeen.databinding.FragmentSelectTimeBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.MedicationsViewModel
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate
import com.cancer.yaqeen.presentation.util.disable
import com.cancer.yaqeen.presentation.util.enable
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SelectTimeFragment : BaseFragment() {

    private var binding: FragmentSelectTimeBinding by autoCleared()

    private lateinit var navController: NavController

    private val medicationsViewModel: MedicationsViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSelectTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setFragmentResultListener(Constants.REQUEST_DATE_KEY) { requestKey, bundle ->
            if(requestKey == Constants.REQUEST_DATE_KEY) {
                val date = bundle.getLong(Constants.DATE_SELECTED_KEY)
                binding.editTextStartFrom.setText(convertMilliSecondsToDate(date))
                medicationsViewModel.selectStartDate(date)

                checkTimeData()
            }
        }

        setFragmentResultListener(Constants.REQUEST_REMINDER_TIME_KEY) { requestKey, bundle ->
            if(requestKey == Constants.REQUEST_REMINDER_TIME_KEY) {
                val reminderTime: ReminderTime? = bundle.getParcelable(Constants.REMINDER_TIME_KEY)
                reminderTime?.run {
                    val timing = if (isAM) getString(R.string.am) else getString(R.string.pm)
                    binding.editTextTime.setText("$text $timing")
                }
                medicationsViewModel.selectReminderTime(reminderTime)

                checkTimeData()
            }
        }

        navController = findNavController()

        setListener()

        updateUI()
    }

    override fun onResume() {
        super.onResume()

        val medicationTrack = medicationsViewModel.getMedicationTrack()
        updateUI(medicationTrack)
    }

    private fun updateUI(medicationTrack: MedicationTrack?) {
        medicationTrack?.run {

            changeCircleColorOfRadioButton()

            if (startDate != null)
                binding.editTextStartFrom.setText(convertMilliSecondsToDate(startDate!!))
            if (reminderTime?.text?.isNotEmpty() == true) {
                val timing = if (reminderTime!!.isAM) getString(R.string.am) else getString(R.string.pm)
                binding.editTextTime.setText("${reminderTime!!.text} $timing")
            }
            if (notes?.isNotEmpty() == true)
                binding.editTextNote.setText(notes)
            if (dosageAmount?.isNotEmpty() == true)
                binding.editTextDosage.setText(dosageAmount)

            binding.toolbar.title = medicationName
            binding.tvMedicationType.text = medicationType?.name ?: ""

            val specificDaysIsNotSelected = periodTime?.id != 5
            binding.itemMedicationTime.container.changeVisibility(specificDaysIsNotSelected)
            binding.textLayoutSpecificDays.changeVisibility(!specificDaysIsNotSelected)

            if(specificDaysIsNotSelected)
                binding.itemMedicationTime.tvMedicationTime.text = periodTime?.time ?: ""
            else
                binding.editTextSpecificDays.setText(specificDays?.joinToString { it.name } ?: "")
        }

        checkTimeData()
    }


    private fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnNext.setOnClickListener {
            val notes = binding.editTextNote.text.toString().trim()
            val dosageAmount = binding.editTextDosage.text.toString().trim()
            medicationsViewModel.selectNotesAndDosageAmount(notes = notes, dosageAmount = dosageAmount)
            navController.tryNavigate(
                SelectTimeFragmentDirections.actionSelectTimeFragmentToMedicationConfirmationFragment()
            )
        }

        binding.editTextStartFrom.setOnClickListener {
            navController.tryNavigate(
                SelectTimeFragmentDirections.actionSelectTimeFragmentToCalendarFragment()
            )
        }
        binding.editTextTime.setOnClickListener {
            navController.tryNavigate(
                SelectTimeFragmentDirections.actionSelectTimeFragmentToTimeFragment()
            )
        }

        binding.editTextNote.addTextChangedListener {
            checkTimeData()
        }

        binding.editTextDosage.addTextChangedListener {
            checkTimeData()
        }
    }

    private fun updateUI() {
        val spannable = SpannableStringBuilder("3/3")
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary_color)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvPageNumber.text = spannable
    }

    private fun changeCircleColorOfRadioButton() {
        binding.itemMedicationTime.btnMedication.isChecked = true

        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked), // unchecked
                intArrayOf(android.R.attr.state_checked)    // checked
            ),
            intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.light_black), // unchecked
                ContextCompat.getColor(requireContext(), R.color.primary_color) // checked
            )
        )

        // Apply color state list to the radio button
        binding.itemMedicationTime.btnMedication.buttonTintList = colorStateList
    }


    private fun checkTimeData(): Boolean {
        val date = binding.editTextStartFrom.text.toString()
        val time = binding.editTextTime.text.toString()
//        val notes = binding.editTextNote.text.toString()
        val dosageAmount = binding.editTextDosage.text.toString()

        val textColorId: Int
        val backgroundColorId: Int

        if(date.isNotEmpty() && time.isNotEmpty() && dosageAmount.isNotEmpty()) {
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

        return false
    }
}