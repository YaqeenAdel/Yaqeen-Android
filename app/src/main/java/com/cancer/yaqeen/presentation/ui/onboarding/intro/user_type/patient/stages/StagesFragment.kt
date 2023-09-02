package com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.patient.stages

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.onboarding.patient.models.CancerType
import com.cancer.yaqeen.data.features.onboarding.patient.models.Stage
import com.cancer.yaqeen.databinding.FragmentStagesBinding
import com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.patient.cancer_type.CancerTypesAdapter
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.HorizontalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryPopBackStack

class StagesFragment : Fragment() {

    private var binding: FragmentStagesBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var stagesAdapter: StagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentStagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setupStagesAdapter()

        binding.tvNext.setOnClickListener {
            navController.tryNavigate(
                StagesFragmentDirections.actionStagesFragmentToModulesFragment()
            )
        }

        binding.tvBack.setOnClickListener {
            navController.tryPopBackStack()
        }
    }
    private fun setupStagesAdapter() {
        stagesAdapter = StagesAdapter {

        }

        binding.rvStages.apply {
            adapter = stagesAdapter
            addItemDecoration(
                HorizontalMarginItemDecoration(
                    dpToPx(16f, requireContext())
                )
            )
        }

        stagesAdapter.submitList(
            listOf(
                Stage(
                    id = 1, stageName = "Stage", number = 1
                ),
                Stage(
                    id = 2, stageName = "Stage", number = 2
                ),
                Stage(
                    id = 3, stageName = "Stage", number = 3
                ),
                Stage(
                    id = 4, stageName = "Stage", number = 4
                ),
                Stage(
                    id = 5, stageName = "Stage", number = 5
                ),
                Stage(
                    id = 6, stageName = "Stage", number = 6
                )
            )
        )
    }
}