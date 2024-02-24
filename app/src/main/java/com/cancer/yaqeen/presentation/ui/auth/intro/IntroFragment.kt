package com.cancer.yaqeen.presentation.ui.auth.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.databinding.FragmentIntroBinding
import com.cancer.yaqeen.presentation.ui.auth.OnboardingViewModel
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.tryNavigate

class IntroFragment : Fragment() {

    private var binding: FragmentIntroBinding by autoCleared()

    private lateinit var navController: NavController

    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentIntroBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()

        val resources = viewModel.viewStateResources.replayCache
        if (resources.isEmpty() && resources.firstOrNull() == null)
            viewModel.getResources()
    }

    private fun setListener(){
        binding.btnStart.setOnClickListener {
            navController.tryNavigate(
                IntroFragmentDirections.actionIntroFragmentToSelectCancerTypeFragment()
            )
        }

        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
    }

}