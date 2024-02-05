package com.cancer.yaqeen.presentation.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentSearchBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment : BaseFragment(), OnClickListener {

    private var binding: FragmentSearchBinding by autoCleared()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()

    }
    private fun setListener(){
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.chipOne.setOnClickListener(this)
        binding.chipTwo.setOnClickListener(this)
        binding.chipThree.setOnClickListener(this)
        binding.chipFour.setOnClickListener(this)
        binding.chipFive.setOnClickListener(this)
        binding.chipSex.setOnClickListener(this)
        binding.chipSeven.setOnClickListener(this)
        binding.chipEight.setOnClickListener(this)
        binding.chipNine.setOnClickListener(this)
        binding.chipTen.setOnClickListener(this)

        binding.editTextArticleName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val articleName = binding.editTextArticleName.text.toString().trim()
                if (articleName.isNotEmpty())
                    navigateToArticles(articleName)
                true
            } else {
                false
            }
        }
    }

    private fun navigateToArticles(name: String) {
        navController.tryNavigate(
            SearchFragmentDirections.actionSearchFragmentToArticlesFragment(name)
        )
    }

    override fun onClick(v: View?) {
        val (interestName, isInterest) = when(v?.id){
            R.id.chip_one -> "Non-Hodgkin’s Lymphoma" to true
            R.id.chip_two -> "Therapy Treatment" to true
            R.id.chip_three -> "Prostate" to true
            R.id.chip_four -> "Lung Cancer" to true
            R.id.chip_five -> "Pancreatic" to true
            R.id.chip_sex -> "Leukaemia" to true
            R.id.chip_seven -> "Kidney" to true
            R.id.chip_eight -> "Testicular" to true
            R.id.chip_nine -> "Multiple Myeloma" to true
            R.id.chip_ten -> "Thyroid" to true
            else -> "" to false
        }

        if (isInterest)
            navigateToArticles(interestName)

    }

}