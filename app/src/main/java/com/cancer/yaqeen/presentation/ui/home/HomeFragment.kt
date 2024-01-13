package com.cancer.yaqeen.presentation.ui.home

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.models.Time
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentHomeBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.home.articles.ArticlesAdapter
import com.cancer.yaqeen.presentation.ui.onboarding.OnboardingViewModel
import com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.doctor.specialization.SpecializationFragmentDirections
import com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.doctor.specialization.university.UniversitiesAdapter
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.HorizontalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryNavigateUp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : BaseFragment(), OnClickListener {

    private var binding: FragmentHomeBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var articlesAdapter: ArticlesAdapter
    private lateinit var timesAdapter: TimesAdapter

    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()
        setupAdapters()
        observeStates()
        getArticles()

        binding.groupProfile.changeVisibility(show = true, isGone = false)
        binding.groupGuest.changeVisibility(show = false, isGone = false)
    }

    private fun setupAdapters(){
        setupArticlesAdapter()
        setupTimesAdapter()
    }

    private fun setupArticlesAdapter() {
        articlesAdapter = ArticlesAdapter {
            navController.tryNavigate(
                HomeFragmentDirections.actionHomeFragmentToArticleDetailsFragment()
            )
        }
        binding.rvArticles.apply {
            adapter = articlesAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(12f, requireContext())
                )
            )
        }
    }

    private fun setupTimesAdapter() {
        timesAdapter = TimesAdapter {

        }
        binding.rvTimes.apply {
            adapter = timesAdapter
        }

        timesAdapter.submitList(
            listOf(
                Time(1, "9:00"),
                Time(2, "10:00"),
                Time(3, "11:00"),
                Time(4, "12:00"),
                Time(5, "1:00"),
                Time(6, "2:00"),
                Time(7, "3:00"),
                Time(8, "4:00")
            )
        )
    }

    private fun observeStates() {
        lifecycleScope {
            homeViewModel.viewStateLoading.collectLatest {
                onLoading(it)
            }
        }
          lifecycleScope {
            homeViewModel.viewStateArticles.collect { articles ->
                articlesAdapter.setList(articles)
             }
        }
     }

    private fun handleResponseError(errorEntity: ErrorEntity?) {
        val errorMessage = handleError(errorEntity)
        displayErrorMessage(errorMessage)
    }

    private fun displayErrorMessage(errorMessage: String?) {
        errorMessage?.let {
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }


    private fun getArticles(){
        homeViewModel.getArticles()
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tv_see_all_calender -> {}
        }
    }

}