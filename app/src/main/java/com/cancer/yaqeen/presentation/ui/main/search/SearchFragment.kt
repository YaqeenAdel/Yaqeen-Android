package com.cancer.yaqeen.presentation.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentSearchBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.disable
import com.cancer.yaqeen.presentation.util.enable
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
        binding.btnConfirm.setOnClickListener(this)

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
        binding.editTextArticleName.addTextChangedListener {
            checkSearchData()
        }
    }

    private fun navigateToArticles(name: String) {
        navController.tryNavigate(
            SearchFragmentDirections.actionSearchFragmentToArticlesFragment(name)
        )
    }

    override fun onClick(v: View?) {
        val (interestName, isInterest) = when(v?.id){
            R.id.chip_one -> getString(R.string.non_hodgkin_s_lymphoma) to true
            R.id.chip_two -> getString(R.string.therapy_treatment) to true
            R.id.chip_three -> getString(R.string.prostate) to true
            R.id.chip_four -> getString(R.string.lung_cancer) to true
            R.id.chip_five -> getString(R.string.pancreatic) to true
            R.id.chip_sex -> getString(R.string.leukaemia) to true
            R.id.chip_seven -> getString(R.string.kidney) to true
            R.id.chip_eight -> getString(R.string.testicular) to true
            R.id.chip_nine -> getString(R.string.multiple_myeloma) to true
            R.id.chip_ten -> getString(R.string.thyroid) to true
            R.id.btn_confirm -> binding.editTextArticleName.text.toString().trim() to true
            else -> "" to false
        }

        if (isInterest)
            navigateToArticles(interestName)

    }

    private fun checkSearchData() {
        val articleName = binding.editTextArticleName.text.toString().trim()

        val textColorId: Int
        val backgroundColorId: Int

        if(articleName.isNotEmpty()) {
            binding.btnConfirm.enable()
            textColorId = R.color.white
            backgroundColorId = R.color.primary_color
        }
        else {
            binding.btnConfirm.disable()
            textColorId = R.color.medium_gray
            backgroundColorId = R.color.light_gray
        }

        binding.btnConfirm.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), backgroundColorId)
        binding.btnConfirm.setTextColor(ContextCompat.getColorStateList(requireContext(), textColorId))
        binding.btnConfirm.iconTint = ContextCompat.getColorStateList(requireContext(), textColorId)

    }
}