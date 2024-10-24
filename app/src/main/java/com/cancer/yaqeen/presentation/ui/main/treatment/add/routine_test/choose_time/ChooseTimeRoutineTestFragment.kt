package com.cancer.yaqeen.presentation.ui.main.treatment.add.routine_test.choose_time

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.ReminderBefore
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.RoutineTestTrack
import com.cancer.yaqeen.databinding.FragmentChooseTimeRoutineTestBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.add.routine_test.RoutineTestViewModel
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImageURI
import com.cancer.yaqeen.presentation.util.calculateStartDateTime
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate
import com.cancer.yaqeen.presentation.util.disable
import com.cancer.yaqeen.presentation.util.enable
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class ChooseTimeRoutineTestFragment : BaseFragment() {

    private var binding: FragmentChooseTimeRoutineTestBinding by autoCleared()

    private lateinit var navController: NavController

    private val routineTestViewModel: RoutineTestViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChooseTimeRoutineTestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setFragmentResultListener(Constants.REQUEST_DATE_KEY) { requestKey, bundle ->
            if(requestKey == Constants.REQUEST_DATE_KEY) {
                val date = bundle.getLong(Constants.DATE_SELECTED_KEY)
                binding.editTextStartFrom.setText(convertMilliSecondsToDate(date))
                routineTestViewModel.selectStartDate(date)

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
                routineTestViewModel.selectReminderTime(reminderTime)

                checkTimeData()
            }
        }

        navController = findNavController()

        setListener()

        updateUI()
    }

    override fun onResume() {
        super.onResume()

        val routineTestTrack = routineTestViewModel.getRoutineTestTrack()
        updateUI(routineTestTrack)
    }

    private fun updateUI(routineTestTrack: RoutineTestTrack?) {
        routineTestTrack?.run {

            changeCircleColorOfRadioButton()

            if (startDate != null)
                binding.editTextStartFrom.setText(convertMilliSecondsToDate(startDate!!))
            if (reminderTime?.text?.isNotEmpty() == true) {
                val timing = if (reminderTime!!.isAM) getString(R.string.am) else getString(R.string.pm)
                binding.editTextTime.setText("${reminderTime!!.text} $timing")
            }
            if (routineTestName?.isNotEmpty() == true) {
                binding.toolbar.title = routineTestName
                binding.tvRoutineDetails.text = routineTestName
            }
            val reminderBeforeTime = getReminderBeforeTime(reminderBefore)
            binding.editTextReminderBeforeTime.setText(reminderBeforeTime)
            if (notes?.isNotEmpty() == true)
                binding.editTextNote.setText(notes)


            val specificDaysIsNotSelected = periodTime?.id != 5
            binding.itemRoutineTestTime.container.changeVisibility(specificDaysIsNotSelected)
            binding.textLayoutSpecificDays.changeVisibility(!specificDaysIsNotSelected)

            if(specificDaysIsNotSelected)
                binding.itemRoutineTestTime.tvMedicationTime.text = periodTime?.timeEn ?: ""
            else
                binding.editTextSpecificDays.setText(specificDays?.joinToString { it.name } ?: "")

            photo?.run {
                uri?.let {
                    updateUI(it)
                }
                url?.let {
                    updateUI(it)
                }
            }
        }

        checkTimeData()
    }


    private fun getReminderBeforeTime(reminderBefore: ReminderBefore): String =
        if (reminderBefore.isMoreThanOrEqualHour)
            getString(R.string.reminder_before_hour, reminderBefore.time)
        else
            getString(R.string.reminder_before_min, reminderBefore.time)

    private fun updateUI(url: String) {
        bindImage(binding.ivRoutine, url)
    }

    private fun updateUI(uri: Uri) {
        bindImageURI(binding.ivRoutine, uri)
    }

    private fun updateUI(reminderBefore: ReminderBefore?) {
        reminderBefore?.run {
            val reminderBeforeTime = getReminderBeforeTime(reminderBefore)
            binding.editTextReminderBeforeTime.setText(reminderBeforeTime)
        }
    }

    private fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnNext.setOnClickListener {
            val isValid = checkOnSelectedDateTime()
            if (!isValid)
                return@setOnClickListener

            val notes = binding.editTextNote.text.toString().trim()

            routineTestViewModel.selectNotes(notes = notes)
            navController.tryNavigate(
                ChooseTimeRoutineTestFragmentDirections.actionChooseTimeRoutineTestFragmentToRoutineTestConfirmationFragment()
            )
        }

        binding.editTextStartFrom.setOnClickListener {
            navController.tryNavigate(
                ChooseTimeRoutineTestFragmentDirections.actionChooseTimeRoutineTestFragmentToCalendarFragment(
                    0L, true
                )
            )
        }
        binding.editTextTime.setOnClickListener {
            navController.tryNavigate(
                R.id.timeFragment
            )
        }

        binding.editTextNote.addTextChangedListener {
            checkTimeData()
        }

        binding.btnIncrease.setOnClickListener {
            val reminderBefore = routineTestViewModel.increaseReminderBefore()
            updateUI(reminderBefore)
        }

        binding.btnDecrease.setOnClickListener {
            val reminderBefore = routineTestViewModel.decreaseReminderBefore()
            updateUI(reminderBefore)
        }
    }

    private fun checkOnSelectedDateTime(): Boolean {
        val routineTestTrack = routineTestViewModel.getRoutineTestTrack()
        val startDate = routineTestTrack?.startDate ?: 0L
        val reminderTime = routineTestTrack?.reminderTime
        val reminderBeforeInMinutes = routineTestTrack?.reminderBefore

        reminderTime?.let {
            val startDateTime = calculateStartDateTime(
                startDate,
                reminderTime.hour24.toIntOrNull() ?: 0,
                reminderTime.minute.toIntOrNull() ?: 0
            )
            val reminderBeforeInMillis = TimeUnit.MINUTES.toMillis(reminderBeforeInMinutes?.timeInMinutes?.toLong() ?: 0)

            if ((startDateTime - reminderBeforeInMillis) < System.currentTimeMillis()) {
                Toast.makeText(requireContext(),
                    getString(R.string.you_must_select_a_new_datetime), Toast.LENGTH_SHORT)
                    .show()
                return false
            }
        }
        return true
    }

    private fun updateUI() {
        val spannable = SpannableStringBuilder("3/3")
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary_color)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvPageNumber.text = spannable
    }

    private fun changeCircleColorOfRadioButton() {
        binding.itemRoutineTestTime.btnMedication.isChecked = true

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
        binding.itemRoutineTestTime.btnMedication.buttonTintList = colorStateList
    }


    private fun checkTimeData(): Boolean {
        val date = binding.editTextStartFrom.text.toString()
        val time = binding.editTextTime.text.toString()
//        val notes = binding.editTextNote.text.toString()

        val textColorId: Int
        val backgroundColorId: Int

        if(date.isNotEmpty() && time.isNotEmpty()) {
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