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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentHomeBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.home.articles.ArticlesAdapter
import com.cancer.yaqeen.presentation.ui.onboarding.OnboardingViewModel
import com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.doctor.specialization.SpecializationFragmentDirections
import com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.doctor.specialization.university.UniversitiesAdapter
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.dpToPx
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

    private val homeViewModel: HomeViewModel by activityViewModels()

    private val handler = Handler()
    private var scrollPosition = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                 requireActivity().finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

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
        setupArticlesAdapter()
        getArticles()
        observeStates()


    }
    private fun setupArticlesAdapter() {
        articlesAdapter = ArticlesAdapter(emptyList()) {

        }
        val llm = LinearLayoutManager(requireContext())
        llm.orientation = LinearLayoutManager.VERTICAL
        binding.rvArticles.setLayoutManager(llm)
        binding.rvArticles.apply {
            adapter = articlesAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(15f, requireContext())
                )
            )
        }

//        universitiesAdapter.setList(
//            listOf(
//                University(
//                    universityID = 1, universityName = "Ain Shams"
//                ),
//                University(
//                    universityID = 2, universityName = "Cairo"
//                ),
//                University(
//                    universityID = 3, universityName = "Zagazig"
//                ),
//                University(
//                    universityID = 4, universityName = "Alexandria"
//                ),
//                University(
//                    universityID = 5, universityName = "Tanta"
//                ),
//                University(
//                    universityID = 6, universityName = "Assuit"
//                ),
//                University(
//                    universityID = 7, universityName = "Aswan"
//                ),
//            )
//        )

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
            R.id.btn_explore_app -> {}
            R.id.btn_join -> {
//                removeCallbacks()
//                navController.tryNavigate(R.id.specializationFragment)
//                navigateToUpdateProfile()
            }
            R.id.tv_login -> {

            }
        }
    }

}