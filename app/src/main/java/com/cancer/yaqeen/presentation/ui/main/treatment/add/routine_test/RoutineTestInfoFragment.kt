package com.cancer.yaqeen.presentation.ui.main.treatment.add.routine_test

import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Day
import com.cancer.yaqeen.data.features.home.schedule.medication.models.DayEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.models.PeriodTimeEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Time
import com.cancer.yaqeen.data.features.home.schedule.routine_test.mappers.MappingRoutineTestAsRoutineTestTrack
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.RoutineTestTrack
import com.cancer.yaqeen.databinding.FragmentRoutineTestInfoBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.add.medical_reminder.MedicalReminderInfoFragmentArgs
import com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.strength.choose_time.DaysAdapter
import com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.strength.choose_time.MedicationTimesAdapter
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImageURI
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.disable
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.enable
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RoutineTestInfoFragment : BaseFragment() {

    private var binding: FragmentRoutineTestInfoBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var medicationTimesAdapter: MedicationTimesAdapter

    private lateinit var daysAdapter: DaysAdapter

    private val routineTestViewModel: RoutineTestViewModel by activityViewModels()

    private val args: RoutineTestInfoFragmentArgs by navArgs()


    private val getContentResultLauncher: ActivityResultLauncher<String?> =
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { result ->

            result?.let {
                setImage(result)
            }

        }

    private val routineTest by lazy {
        args.routineTest
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (routineTest == null)
            routineTestViewModel.resetRoutineTestTrack()
        else
            routineTestViewModel.setRoutineTestTrack(
                MappingRoutineTestAsRoutineTestTrack(requireContext()).map(routineTest!!)
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRoutineTestInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(Constants.REQUEST_METHOD_ATTACHED_FILE_KEY) { requestKey, bundle ->
            if (requestKey == Constants.REQUEST_METHOD_ATTACHED_FILE_KEY) {
                val fileMethod = bundle.getBoolean(Constants.FILE_KEY)

                if (fileMethod)
                    openStorage()

            }
        }

        setFragmentResultListener(Constants.REQUEST_TAKE_PICTURE_KEY) { requestKey, bundle ->
            if (requestKey == Constants.REQUEST_TAKE_PICTURE_KEY) {
                val imageUri = bundle.getString(Constants.IMAGE_URI_KEY)
                val uri = imageUri?.toUri()

                uri?.let {
                    setImage(uri)
                }
            }
        }

        navController = findNavController()

        setupAdapters()

        updateUI()

        setListener()

    }

    override fun onResume() {
        super.onResume()

        val routineTestTrack = routineTestViewModel.getRoutineTestTrack()
        updateUI(routineTestTrack)
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
            val routineTestName = binding.editTextTestName.text.toString().trim()
            routineTestViewModel.setRoutineTestInfo(
                routineTestName = routineTestName,
                periodTime = medicationTimesAdapter.getItemSelected(),
                specificDays = daysAdapter.getItemsSelected()
            )
            navController.tryNavigate(
                RoutineTestInfoFragmentDirections.actionRoutineTestInfoFragmentToChooseTimeRoutineTestFragment()
            )
        }

        binding.btnAttachImage.setOnClickListener {
            navController.tryNavigate(
                R.id.methodAttachedFileFragment
            )
        }

        binding.btnShow.setOnClickListener {
            val photo = routineTestViewModel.getRoutinePhoto()
            navController.tryNavigate(
                R.id.photoFullScreenFragment, bundleOf(
                    Constants.IMAGE_URI to photo?.uri?.toString(),
                    Constants.IMAGE_URL to photo?.url,
                )
            )
        }
        binding.btnDelete.setOnClickListener {
            setImage(null)
            routineTestViewModel.deleteRoutinePhoto()
        }

        binding.editTextTestName.addTextChangedListener {
            checkPeriodTimeData()
        }
    }

    private fun updateUI(routineTestTrack: RoutineTestTrack?) {
        routineTestTrack?.run {
            if (routineTestName?.isNotEmpty() == true)
                binding.editTextTestName.setText(routineTestName)

            val imageIsAttached = photo?.uri != null || photo?.url != null
            updateUI(imageIsAttached)

            photo?.run {
                binding.tvRoutineImageName.text = imageName
                uri?.let {
                    updateUI(it)
                }
                url?.let {
                    updateUI(it)
                }
            }

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

    private fun updateUI(url: String) {
        bindImage(binding.ivRoutine, url)
    }

    private fun updateUI(uri: Uri) {
        bindImageURI(binding.ivRoutine, uri)
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

    private fun updateUI(imageIsAttached: Boolean){
        binding.groupImageAttached.changeVisibility(show = imageIsAttached, isGone = true)
        binding.groupAttachImage.changeVisibility(show = !imageIsAttached, isGone = true)
    }
    private fun setImage(uri: Uri?) {
        uri?.let {
            val photo = routineTestViewModel.createPhoto(uri)
            binding.tvRoutineImageName.text = photo.imageName
            routineTestViewModel.addRoutinePhoto(photo)
        }
        updateUI(uri != null)
        bindImageURI(binding.ivRoutine, uri)

    }

    private fun setupDaysAdapter() {
        daysAdapter = DaysAdapter {
            checkPeriodTimeData()
        }

        binding.rvDays.adapter = daysAdapter

        daysAdapter.setList(
            listOf(
                Day(
                    id = DayEnum.SUN.id,
                    name = getString(R.string.sun),
                    cronExpression = DayEnum.SUN.cronExpression
                ),
                Day(
                    id = DayEnum.MON.id,
                    name = getString(R.string.mon),
                    cronExpression = DayEnum.MON.cronExpression
                ),
                Day(
                    id = DayEnum.TUE.id,
                    name = getString(R.string.tues),
                    cronExpression = DayEnum.TUE.cronExpression
                ),
                Day(
                    id = DayEnum.WED.id,
                    name = getString(R.string.wed),
                    cronExpression = DayEnum.WED.cronExpression
                ),
                Day(
                    id = DayEnum.THU.id,
                    name = getString(R.string.thur),
                    cronExpression = DayEnum.THU.cronExpression
                ),
                Day(
                    id = DayEnum.FRI.id,
                    name = getString(R.string.fri),
                    cronExpression = DayEnum.FRI.cronExpression
                ),
                Day(
                    id = DayEnum.SAT.id,
                    name = getString(R.string.sat),
                    cronExpression = DayEnum.SAT.cronExpression
                )
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
            displayDays(it.id)
            checkPeriodTimeData()
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
                    id = PeriodTimeEnum.EVERY_DAY.id,
                    time = getString(R.string.every_day),
                    cronExpression = PeriodTimeEnum.EVERY_DAY.cronExpression
                ),
                Time(
                    id = PeriodTimeEnum.EVERY_12_HOURS.id,
                    time = getString(R.string.every_12_hours),
                    cronExpression = PeriodTimeEnum.EVERY_12_HOURS.cronExpression
                ),
                Time(
                    id = PeriodTimeEnum.DAY_AFTER_DAY.id,
                    time = getString(R.string.day_after_day),
                    cronExpression = PeriodTimeEnum.DAY_AFTER_DAY.cronExpression
                ),
                Time(
                    id = PeriodTimeEnum.EVERY_WEEK.id,
                    time = getString(R.string.every_week),
                    cronExpression = PeriodTimeEnum.EVERY_WEEK.cronExpression
                ),
                Time(
                    id = PeriodTimeEnum.EVERY_MONTH.id,
                    time = getString(R.string.every_month),
                    cronExpression = PeriodTimeEnum.EVERY_MONTH.cronExpression
                ),
                Time(
                    id = PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id,
                    time = getString(R.string.specific_days_of_the_week),
                    cronExpression = PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.cronExpression
                )
            )
        )
    }

    private fun openStorage() {
        getContentResultLauncher.launch("image/*")
    }

    private fun displayDays(id: Int): Boolean {
        val specificDaysIsNotSelected = id != PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id
        binding.groupSpecificDays.changeVisibility(show = !specificDaysIsNotSelected, isGone = true)

        return specificDaysIsNotSelected
    }

    private fun checkPeriodTimeData() {
        val routineTestName = binding.editTextTestName.text.toString().trim()

        val textColorId: Int
        val backgroundColorId: Int

        val selectedPositionTime = medicationTimesAdapter.selectedPosition()

        if (
            routineTestName.isNotEmpty() &&
            ((selectedPositionTime != -1 && selectedPositionTime != 5)
                            || (selectedPositionTime == 5 && daysAdapter.anyItemIsSelected()))
        ) {
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