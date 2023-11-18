package com.cancer.yaqeen.presentation.ui.onboarding.activation

import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentActivationBinding
import com.cancer.yaqeen.databinding.FragmentOnBoardingBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.onboarding.OnBoardingFragmentDirections
import com.cancer.yaqeen.presentation.ui.onboarding.OnboardingViewModel
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.formatMilliseconds
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivationFragment : BaseFragment(), View.OnClickListener {

    private var binding: FragmentActivationBinding by autoCleared()

    private lateinit var navController: NavController

    private val viewModel: OnboardingViewModel by activityViewModels()

    private lateinit var timer: CountDownTimer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentActivationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()


        updateUI()

        setListener()

        setupCountDown()
    }

    private fun setListener(){
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
        binding.btnResendAgain.setOnClickListener(this)
    }
    private fun updateUI() {
        binding.tvEdit.paintFlags = binding.tvEdit.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        viewModel.getUser()?.apply {
            binding.tvEmail.text = email ?: ""
        }
    }

    private fun setupCountDown() {
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(p0: Long) {
                binding.tvExpireTimeCode.text = p0.formatMilliseconds()
            }

            override fun onFinish() {
                if(isVisible) {
                    binding.linearLayoutTime.changeVisibility(false)
                    binding.btnResendAgain.changeVisibility(true)
                }
            }

        }

        startTimer()
    }

    private fun startTimer() {
        timer.start()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_resend_again -> {
                binding.linearLayoutTime.changeVisibility(true)
                binding.btnResendAgain.changeVisibility(false)
                startTimer()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        if(::timer.isInitialized)
            timer.cancel()
    }

}