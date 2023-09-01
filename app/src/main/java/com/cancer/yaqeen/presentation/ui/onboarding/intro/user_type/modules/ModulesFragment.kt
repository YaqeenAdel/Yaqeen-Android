package com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.modules

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.onboarding.patient.models.Module
import com.cancer.yaqeen.data.features.onboarding.patient.models.Stage
import com.cancer.yaqeen.databinding.FragmentModulesBinding
import com.cancer.yaqeen.databinding.FragmentStagesBinding
import com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.patient.stages.StagesAdapter
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.CenterGridMarginItemDecoration
import com.cancer.yaqeen.presentation.util.recyclerview.GridMarginItemDecoration
import com.cancer.yaqeen.presentation.util.recyclerview.HorizontalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryPopBackStack

class ModulesFragment : Fragment() {

    private var binding: FragmentModulesBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var modulesAdapter: ModulesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentModulesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setupModulesAdapter()

        binding.tvNext.setOnClickListener {

        }

        binding.tvBack.setOnClickListener {
            navController.tryPopBackStack()
        }
    }
    private fun setupModulesAdapter() {
        modulesAdapter = ModulesAdapter {

        }

        binding.rvModules.apply {
            adapter = modulesAdapter
            addItemDecoration(
                GridMarginItemDecoration(
                    dpToPx(16f, requireContext())
                )
            )
        }

        modulesAdapter.submitList(
            listOf(
                Module(
                    id = 1, moduleName = "Stage"
                ),
                Module(
                    id = 2, moduleName = "Stage"
                ),
                Module(
                    id = 3, moduleName = "Stage"
                ),
                Module(
                    id = 4, moduleName = "Stage"
                )
            )
        )
    }
}