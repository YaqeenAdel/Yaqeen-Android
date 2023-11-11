package com.cancer.yaqeen.presentation.ui.onboarding.activation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentActivationBinding
import com.cancer.yaqeen.databinding.FragmentOnBoardingBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.onboarding.OnBoardingFragmentDirections
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivationFragment : BaseFragment(), View.OnClickListener {
    private var binding: FragmentActivationBinding by autoCleared()
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentActivationBinding.inflate(inflater, container, false)
        return binding.root    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()

    }
    private fun navigateToProfile() {
         navController.tryNavigate(
            ActivationFragmentDirections.actionActivationFragmentToProfileFragment()
        )
    }
    private fun setListener() {
        binding.ivProfile.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        when(v?.id){

            R.id.iv_profile -> {navigateToProfile()}
    }
    }


}