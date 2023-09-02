package com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.patient.cancer_type

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.onboarding.patient.models.CancerType
import com.cancer.yaqeen.databinding.FragmentSelectCancerTypeBinding
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryPopBackStack

class SelectCancerTypeFragment : Fragment() {

    private var binding: FragmentSelectCancerTypeBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var cancerTypesAdapter: CancerTypesAdapter

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

        setupCancerTypesAdapter()

        binding.tvNext.setOnClickListener {
            navController.tryNavigate(
                SelectCancerTypeFragmentDirections.actionSelectCancerTypeFragmentToStagesFragment()
            )
        }

        binding.tvBack.setOnClickListener {
            navController.tryPopBackStack()
        }
    }

    private fun setupCancerTypesAdapter() {
        cancerTypesAdapter = CancerTypesAdapter {

        }

        binding.rvCancerTypes.apply {
            adapter = cancerTypesAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(15f, requireContext())
                )
            )
        }

        cancerTypesAdapter.submitList(
            listOf(
                CancerType(
                    id = 1, typeName = "Selection item"
                ),
                CancerType(
                    id = 2, typeName = "Selection item"
                ),
                CancerType(
                    id = 3, typeName = "Selection item"
                ),
                CancerType(
                    id = 4, typeName = "Selection item"
                ),
                CancerType(
                    id = 5, typeName = "Other "
                ),
            )
        )
    }
}