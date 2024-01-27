package com.cancer.yaqeen.presentation.ui.main.treatment

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.models.Time
import com.cancer.yaqeen.data.utils.getTodayDate
import com.cancer.yaqeen.databinding.FragmentMoreBinding
import com.cancer.yaqeen.databinding.FragmentTreatmentBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.MainActivity
import com.cancer.yaqeen.presentation.ui.main.more.MoreFragmentDirections
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.Duration
import com.yuyakaido.android.cardstackview.RewindAnimationSetting
import com.yuyakaido.android.cardstackview.StackFrom
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TreatmentFragment : BaseFragment(showBottomMenu = true), View.OnClickListener {

    private var binding: FragmentTreatmentBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var timesAdapter: TimesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTreatmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()
        updateUI()

        setupTimesAdapter()

    }

    override fun onResume() {
        super.onResume()

        binding.tvCurrentDayDate.text = getTodayDate()
    }

    private fun setListener(){
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
        binding.btnMedications.setOnClickListener(this)
        binding.btnSymptoms.setOnClickListener(this)

    }
    private fun updateUI() {
        val spannable = SpannableStringBuilder("1/3")
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary_color)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvPageNumber.text = spannable

    }

    private fun setupTimesAdapter() {
        timesAdapter = TimesAdapter {

        }

        binding.rvTimes.adapter = timesAdapter


        timesAdapter.submitList(
            listOf(
                Time(0, "00:00"),
                Time(1, "1:00"),
                Time(2, "2:00"),
                Time(3, "3:00"),
                Time(4, "4:00"),
                Time(5, "5:00"),
                Time(6, "6:00"),
                Time(7, "7:00"),
                Time(8, "8:00"),
                Time(9, "9:00"),
                Time(10, "10:00"),
                Time(11, "11:00"),
                Time(12, "12:00"),
                Time(13, "13:00"),
                Time(14, "14:00"),
                Time(15, "15:00"),
                Time(16, "16:00"),
                Time(17, "17:00"),
                Time(18, "18:00"),
                Time(19, "19:00"),
                Time(20, "20:00"),
                Time(21, "21:00"),
                Time(22, "22:00"),
                Time(23, "23:00")
            )
        )

        selectItem(12)
    }

    private fun selectItem(itemId: Int) {
        val selectItemPosition = timesAdapter.selectItem(itemId)
        if(selectItemPosition >= 2)
            binding.rvTimes.scrollToPosition(selectItemPosition - 2)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_medications -> {
                navController.tryNavigate(
                    TreatmentFragmentDirections.actionTreatmentFragmentToMedicationsFragment()
                )
            }
            R.id.btn_symptoms -> {
//                navController.tryNavigate(
//
//                )
            }
        }
    }
}