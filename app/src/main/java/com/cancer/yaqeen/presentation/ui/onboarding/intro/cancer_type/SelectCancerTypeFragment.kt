package com.cancer.yaqeen.presentation.ui.onboarding.intro.cancer_type

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentSelectCancerTypeBinding
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.tryPopBackStack

class SelectCancerTypeFragment : Fragment() {

    private var binding: FragmentSelectCancerTypeBinding by autoCleared()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSelectCancerTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.tvNext.setOnClickListener {

        }

        binding.tvBack.setOnClickListener {
            navController.tryPopBackStack()
        }
    }
}