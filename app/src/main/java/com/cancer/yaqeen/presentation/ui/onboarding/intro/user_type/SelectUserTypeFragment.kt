package com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentIntroBinding
import com.cancer.yaqeen.databinding.FragmentSelectUserTypeBinding
import com.cancer.yaqeen.presentation.ui.onboarding.OnboardingViewModel
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectUserTypeFragment : Fragment() {


    private var binding: FragmentSelectUserTypeBinding by autoCleared()

    private lateinit var navController: NavController

    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSelectUserTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.tvNext.setOnClickListener {
            val isPatient = binding.btnPatient.isChecked
            viewModel.selectUser(isPatient)
            if(isPatient)
                navController.tryNavigate(
                    SelectUserTypeFragmentDirections.actionSelectUserTypeFragmentToSelectCancerTypeFragment()
                )
            else
                navController.tryNavigate(
                    SelectUserTypeFragmentDirections.actionSelectUserTypeFragmentToSpecializationFragment()
                )
        }

        binding.tvBack.setOnClickListener {
            navController.tryPopBackStack()
        }
    }

}