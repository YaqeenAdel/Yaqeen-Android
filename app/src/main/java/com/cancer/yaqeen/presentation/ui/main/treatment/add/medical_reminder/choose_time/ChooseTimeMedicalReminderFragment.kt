package com.cancer.yaqeen.presentation.ui.main.treatment.add.medical_reminder.choose_time

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
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
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models.MedicalReminderTrack
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.ReminderBefore
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.Symptom
import com.cancer.yaqeen.databinding.FragmentChooseTimeMedicalReminderBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.add.medical_reminder.MedicalReminderViewModel
import com.cancer.yaqeen.presentation.ui.main.treatment.history.adapters.SmallPhotosAdapter
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.calculateStartDateTime
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate
import com.cancer.yaqeen.presentation.util.disable
import com.cancer.yaqeen.presentation.util.enable
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class ChooseTimeMedicalReminderFragment : BaseFragment() {

    private var binding: FragmentChooseTimeMedicalReminderBinding by autoCleared()

    private lateinit var navController: NavController

    private val medicalReminderViewModel: MedicalReminderViewModel by activityViewModels()


    private lateinit var photosAdapter: SmallPhotosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChooseTimeMedicalReminderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(Constants.REQUEST_DATE_KEY) { requestKey, bundle ->
            if (requestKey == Constants.REQUEST_DATE_KEY) {
                val date = bundle.getLong(Constants.DATE_SELECTED_KEY)
                setDate(date)
            }
        }

        setFragmentResultListener(Constants.REQUEST_REMINDER_TIME_KEY) { requestKey, bundle ->
            if (requestKey == Constants.REQUEST_REMINDER_TIME_KEY) {
                val reminderTime: ReminderTime? = bundle.getParcelable(Constants.REMINDER_TIME_KEY)
                setReminderTime(reminderTime)
            }
        }

        setFragmentResultListener(Constants.REQUEST_SYMPTOM_KEY) { requestKey, bundle ->
            if (requestKey == Constants.REQUEST_SYMPTOM_KEY) {
                val symptom: Symptom? = bundle.getParcelable(Constants.SYMPTOM_KEY)

                updateUI(symptom)
                medicalReminderViewModel.setSymptom(symptom)
            }
        }

        navController = findNavController()

        updateUI()

        setListener()

    }

    private fun setReminderTime(reminderTime: ReminderTime?) {
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
        medicalReminderViewModel.selectReminderTime(reminderTime)

        checkMedicalReminderTimeData()
    }

    override fun onResume() {
        super.onResume()

        val medicalReminderTrack = medicalReminderViewModel.getMedicalReminderTrack()
        updateUI(medicalReminderTrack)
    }

    private fun setDate(date: Long) {
        val startDate = convertMilliSecondsToDate(date)
        binding.editTextStartFrom.setText(startDate)
        medicalReminderViewModel.selectStartDate(date)

        checkMedicalReminderTimeData()
    }

    private fun updateUI(medicalReminderTrack: MedicalReminderTrack?){
        medicalReminderTrack?.run {
            binding.toolbar.title = doctorName
            binding.tvDoctorPhoneNumber.text = phoneNumber
            binding.tvDoctorAddress.text = location

            startDate?.let {
                binding.editTextStartFrom.setText(convertMilliSecondsToDate(startDate!!))
            }

            reminderTime?.run {
                val timing = if (isAM) getString(R.string.am) else getString(R.string.pm)
                binding.editTextTime.setText("$text $timing")
            }

            val reminderBeforeTime = getReminderBeforeTime(reminderBefore)
            binding.editTextReminderBeforeTime.setText(reminderBeforeTime)
            if (notes?.isNotEmpty() == true) {
                binding.editTextNote.setText(notes)
            }

            updateUI(symptom)


        }
        checkMedicalReminderTimeData()
    }

    private fun updateUI(symptom: Symptom?){
        val symptomIsSelected = symptom != null

        binding.groupSymptom.changeVisibility(show = symptomIsSelected, isGone = true)
        binding.btnAddSymptom.changeVisibility(show = !symptomIsSelected, isGone = true)

        symptom?.run {
            val types = symptomTypes?.joinToString(separator = "\n"){ it.name } ?: ""
            val isReminder = doctorName?.isNotEmpty() == true

            bindImage(binding.itemSymptom.ivSymptom, photosList?.firstOrNull()?.url)

            photosList?.let {
                setupPhotosAdapter(photosList!!.map { it.url ?: "" })
            }

            binding.itemSymptom.tvSymptomTypes.text = types
            binding.itemSymptom.tvSymptomsVal.text = types
            binding.itemSymptom.tvNotesVal.text = details
            binding.itemSymptom.tvReminderVal.text = doctorName ?: ""
            binding.itemSymptom.tvReminder.changeVisibility(show = isReminder, isGone = true)
            binding.itemSymptom.tvReminderVal.changeVisibility(show = isReminder, isGone = true)
            binding.itemSymptom.tvDateTimeVal.text = "$reminderTime - $startDate"

            binding.itemSymptom.layoutLess.changeVisibility(show = true)
            binding.itemSymptom.layoutMore.changeVisibility(show = false, isGone = true)

            binding.itemSymptom.linearLayout.changeVisibility(show = false, isGone = true)
        }
    }

    private fun updateUI(reminderBefore: ReminderBefore?) {
        reminderBefore?.run {
            val reminderBeforeTime = getReminderBeforeTime(reminderBefore)
            binding.editTextReminderBeforeTime.setText(reminderBeforeTime)
        }
    }

    private fun getReminderBeforeTime(reminderBefore: ReminderBefore): String =
        if (reminderBefore.isMoreThanOrEqualHour)
            getString(R.string.reminder_before_hour, reminderBefore.time)
        else
            getString(R.string.reminder_before_min, reminderBefore.time)

    private fun setupPhotosAdapter(photosList: List<String>) {
        photosAdapter = SmallPhotosAdapter {

        }

        binding.itemSymptom.rvSymptomPhotos.apply {
            adapter = photosAdapter
        }

        photosAdapter.submitList(photosList)

    }

    private fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
        binding.btnAddSymptom.setOnClickListener {
            navController.tryNavigate(
                ChooseTimeMedicalReminderFragmentDirections.actionChooseTimeMedicalReminderFragmentToSymptomsHistoryFragment()
            )
        }
        binding.btnNext.setOnClickListener {

            val isValid = checkOnSelectedDateTime()
            if (!isValid)
                return@setOnClickListener

            saveMedicalData()

            navController.tryNavigate(
                ChooseTimeMedicalReminderFragmentDirections.actionChooseTimeMedicalReminderFragmentToMedicalReminderConfirmationFragment()
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

        binding.editTextReminderBeforeTime.addTextChangedListener {
            checkMedicalReminderTimeData()
        }

        binding.itemSymptom.btnShowMore.setOnClickListener {
            updateUI(displayMore = true)
        }

        binding.itemSymptom.btnShowLess.setOnClickListener {
            updateUI(displayMore = false)
        }

        binding.btnDeleteSymptom.setOnClickListener {
            updateUI(symptom = null)
            medicalReminderViewModel.setSymptom(null)
        }

        binding.btnIncrease.setOnClickListener {
            val reminderBefore = medicalReminderViewModel.increaseReminderBefore()
            updateUI(reminderBefore)
        }

        binding.btnDecrease.setOnClickListener {
            val reminderBefore = medicalReminderViewModel.decreaseReminderBefore()
            updateUI(reminderBefore)
        }
    }


    private fun checkOnSelectedDateTime(): Boolean {
        val medicalReminderTrack = medicalReminderViewModel.getMedicalReminderTrack()
        val startDate = medicalReminderTrack?.startDate ?: 0L
        val reminderTime = medicalReminderTrack?.reminderTime
        val reminderBeforeInMinutes = medicalReminderTrack?.reminderBefore

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

    private fun saveMedicalData() {
        val notes = binding.editTextNote.text.toString()

        medicalReminderViewModel.setNotes(
            notes = notes
        )
    }

    private fun updateUI(displayMore: Boolean){
        binding.itemSymptom.layoutLess.changeVisibility(show = !displayMore, isGone = true)
        binding.itemSymptom.layoutMore.changeVisibility(show = displayMore, isGone = true)
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
    private fun checkMedicalReminderTimeData() {
        val date = binding.editTextStartFrom.text.toString()
        val time = binding.editTextTime.text.toString()
        val reminderBeforeTime = binding.editTextReminderBeforeTime.text.toString()

        val textColorId: Int
        val backgroundColorId: Int

        if (date.isNotEmpty() && time.isNotEmpty() && reminderBeforeTime.isNotEmpty()) {
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