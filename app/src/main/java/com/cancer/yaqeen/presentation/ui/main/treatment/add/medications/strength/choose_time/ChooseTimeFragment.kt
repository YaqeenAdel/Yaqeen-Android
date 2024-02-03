package com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.strength.choose_time

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
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Day
import com.cancer.yaqeen.data.features.home.schedule.medication.models.DayEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.models.MedicationTrack
import com.cancer.yaqeen.data.features.home.schedule.medication.models.PeriodTimeEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Time
import com.cancer.yaqeen.databinding.FragmentChooseTimeBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.MedicationsViewModel
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate
import com.cancer.yaqeen.presentation.util.disable
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.enable
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseTimeFragment : BaseFragment() {

    private var binding: FragmentChooseTimeBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var medicationTimesAdapter: MedicationTimesAdapter

    private lateinit var daysAdapter: DaysAdapter

    private val medicationsViewModel: MedicationsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChooseTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(Constants.REQUEST_DATE_KEY) { requestKey, bundle ->
            if (requestKey == Constants.REQUEST_DATE_KEY) {
                val date = bundle.getLong(Constants.DATE_SELECTED_KEY)
                binding.editTextStartFrom.setText(convertMilliSecondsToDate(date))
                medicationsViewModel.selectStartDate(date)

                checkPeriodTimeData()
            }
        }

        setFragmentResultListener(Constants.REQUEST_REMINDER_TIME_KEY) { requestKey, bundle ->
            if (requestKey == Constants.REQUEST_REMINDER_TIME_KEY) {
                val reminderTime: ReminderTime? = bundle.getParcelable(Constants.REMINDER_TIME_KEY)
                reminderTime?.run {
                    binding.editTextTime.setText(text)
                }
                medicationsViewModel.selectReminderTime(reminderTime)

                checkPeriodTimeData()
            }
        }

        navController = findNavController()

        setupAdapters()

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
            if (startDate != null)
                binding.editTextStartFrom.setText(convertMilliSecondsToDate(startDate!!))
            if (reminderTime?.text?.isNotEmpty() == true)
                binding.editTextTime.setText(reminderTime!!.text)
            if (notes?.isNotEmpty() == true)
                binding.editTextNote.setText(notes)
            if (dosageAmount?.isNotEmpty() == true)
                binding.editTextDosage.setText(dosageAmount)

            binding.toolbar.title = medicationName
            binding.tvMedicationType.text = medicationType?.name ?: ""

            periodTime?.run {
                medicationTimesAdapter.selectItem(id)
                val specificDaysIsNotSelected = displayDays(id)
                if (!specificDaysIsNotSelected) {
                    daysAdapter.selectItems(specificDays)
                }
            }
        }

        checkPeriodTimeData()
    }

    private fun setupAdapters() {
        setupMedicationTimesAdapter()
        setupDaysAdapter()
    }

    private fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnNext.setOnClickListener {
            val notes = binding.editTextNote.text.toString().trim()
            val dosageAmount = binding.editTextDosage.text.toString().trim()
            medicationsViewModel.selectPeriodTime(
                periodTime = medicationTimesAdapter.getItemSelected(),
                specificDays = daysAdapter.getItemsSelected(),
                notes = notes,
                dosageAmount = dosageAmount,
            )
            navController.tryNavigate(
                ChooseTimeFragmentDirections.actionChooseTimeFragmentToSelectTimeFragment()
            )
        }
        binding.editTextStartFrom.setOnClickListener {
            navController.tryNavigate(
                ChooseTimeFragmentDirections.actionChooseTimeFragmentToCalendarFragment()
            )
        }
        binding.editTextTime.setOnClickListener {
            navController.tryNavigate(
                ChooseTimeFragmentDirections.actionChooseTimeFragmentToTimeFragment()
            )
        }

        binding.editTextNote.addTextChangedListener {
            checkPeriodTimeData()
        }

        binding.editTextDosage.addTextChangedListener {
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

    private fun setupDaysAdapter() {
        daysAdapter = DaysAdapter {
            checkPeriodTimeData()
        }

        binding.rvDays.adapter = daysAdapter

        daysAdapter.setList(
            listOf(
                Day(id = DayEnum.SUN.id, name = getString(R.string.sun), cronExpression = DayEnum.SUN.cronExpression),
                Day(id = DayEnum.MON.id, name = getString(R.string.mon), cronExpression = DayEnum.MON.cronExpression),
                Day(id = DayEnum.TUE.id, name = getString(R.string.tues), cronExpression = DayEnum.TUE.cronExpression),
                Day(id = DayEnum.WED.id, name = getString(R.string.wed), cronExpression = DayEnum.WED.cronExpression),
                Day(id = DayEnum.THU.id, name = getString(R.string.thur), cronExpression = DayEnum.THU.cronExpression),
                Day(id = DayEnum.FRI.id, name = getString(R.string.fri), cronExpression = DayEnum.FRI.cronExpression),
                Day(id = DayEnum.SAT.id, name = getString(R.string.sat), cronExpression = DayEnum.SAT.cronExpression)
            )
        )
    }

    private fun selectItem(itemId: Int) {
        val selectItemPosition = daysAdapter.selectItem(itemId)
        if (selectItemPosition >= Constants.MAX_POSITION_TO_SCROLL)
            binding.rvTimes.scrollToPosition(selectItemPosition - Constants.MAX_POSITION_TO_SCROLL)
    }

    private fun setupMedicationTimesAdapter() {
        medicationTimesAdapter = MedicationTimesAdapter {
            val specificDaysIsNotSelected = displayDays(it.id)

            if (specificDaysIsNotSelected) {
                medicationsViewModel.selectPeriodTime(periodTime = it)
                navController.tryNavigate(
                    ChooseTimeFragmentDirections.actionChooseTimeFragmentToSelectTimeFragment()
                )
            } else {
                checkPeriodTimeData()
            }
        }

        binding.rvTimes.apply {
            adapter = medicationTimesAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(12f, requireContext())
                )
            )
        }

        medicationTimesAdapter.submitList(
            listOf(
                Time(
                    id = PeriodTimeEnum.EVERY_DAY.id, time = getString(R.string.every_day), cronExpression = PeriodTimeEnum.EVERY_DAY.cronExpression
                ),
                Time(
                    id = PeriodTimeEnum.EVERY_8_HOURS.id, time = getString(R.string.every_8_hours), cronExpression = PeriodTimeEnum.EVERY_8_HOURS.cronExpression
                ),
                Time(
                    id = PeriodTimeEnum.EVERY_12_HOURS.id, time = getString(R.string.every_12_hours), cronExpression = PeriodTimeEnum.EVERY_12_HOURS.cronExpression
                ),
                Time(
                    id = PeriodTimeEnum.DAY_AFTER_DAY.id, time = getString(R.string.day_after_day), cronExpression = PeriodTimeEnum.DAY_AFTER_DAY.cronExpression
                ),
                Time(
                    id = PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id, time = getString(R.string.specific_days_of_the_week), cronExpression = PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.cronExpression
                )
            )
        )
    }

    private fun displayDays(id: Int): Boolean {
        val specificDaysIsNotSelected = id != 5
        binding.groupSpecificDays.changeVisibility(show = !specificDaysIsNotSelected, isGone = true)

        return specificDaysIsNotSelected
    }

    private fun checkPeriodTimeData() {
        val date = binding.editTextStartFrom.text.toString()
        val time = binding.editTextTime.text.toString()
        val notes = binding.editTextNote.text.toString()
        val dosageAmount = binding.editTextDosage.text.toString()

        val textColorId: Int
        val backgroundColorId: Int

        if (date.isNotEmpty() && time.isNotEmpty() && medicationTimesAdapter.selectedPosition() == 4 && daysAdapter.anyItemIsSelected() && notes.isNotEmpty() && dosageAmount.isNotEmpty()) {
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