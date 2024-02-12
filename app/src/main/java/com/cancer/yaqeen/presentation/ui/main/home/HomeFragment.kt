package com.cancer.yaqeen.presentation.ui.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.home.articles.models.Article
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.data.utils.getTodayDate
import com.cancer.yaqeen.databinding.FragmentHomeBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.home.articles.ArticlesAdapter
import com.cancer.yaqeen.presentation.ui.main.treatment.history.MedicationsAdapter
import com.cancer.yaqeen.presentation.ui.main.treatment.history.TreatmentHistoryFragmentDirections
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.Duration
import com.yuyakaido.android.cardstackview.RewindAnimationSetting
import com.yuyakaido.android.cardstackview.StackFrom
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class HomeFragment : BaseFragment(showBottomMenu = true) {

    private var binding: FragmentHomeBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var articlesAdapter: ArticlesAdapter

    private lateinit var schedulesAdapter: SchedulesAdapter

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

        setListener()
    }

    private fun setupAdapters() {
        setupArticlesAdapter()
        setupSchedulesAdapter()
    }

    override fun onResume() {
        super.onResume()

        updateUI()

        homeViewModel.getArticles()
        homeViewModel.getTodayReminders()
    }
    private fun setListener(){
        binding.tvSeeAll.setOnClickListener {
            navController.tryNavigate(
                HomeFragmentDirections.actionHomeFragmentToTreatmentHistoryFragment()
            )
        }
    }

    private fun updateUI() {
        val isLogged = homeViewModel.userIsLoggedIn()
        val user = homeViewModel.getUser()

        binding.groupProfile.changeVisibility(show = isLogged, isGone = false)
        binding.groupGuest.changeVisibility(show = !isLogged, isGone = false)


        binding.tvNameUser.text = user?.name ?: ""
        bindImage(binding.ivProfilePic, user?.pictureURL)


        binding.tvCurrentDayDate.text = getTodayDate()
    }

    private fun setupArticlesAdapter() {
        articlesAdapter = ArticlesAdapter(
            onItemClick = {
                navController.tryNavigate(
                    HomeFragmentDirections.actionHomeFragmentToArticleDetailsFragment(it, false)
                )
            },
            onFavouriteArticleClick = {
                if (homeViewModel.userIsLoggedIn())
                    homeViewModel.changeFavouriteStatusArticle(it)
                else
                    navController.tryNavigate(R.id.authFragment)
            }
        )
        binding.rvArticles.apply {
            adapter = articlesAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(12f, requireContext())
                )
            )
        }
    }

    private fun setupSchedulesAdapter() {
        schedulesAdapter = SchedulesAdapter(
            onItemClick = {
                navController.tryNavigate(
                    TreatmentHistoryFragmentDirections.actionTreatmentHistoryFragmentToMedicationDialogFragment(it)
                )
            }
        )
        binding.rvSchedules.apply {
            adapter = schedulesAdapter
            layoutManager = createCardStackLayoutManager()
        }
        binding.rvSchedules.swipe()
    }

    private fun createCardStackLayoutManager(): CardStackLayoutManager {
        val setting = RewindAnimationSetting.Builder()
            .setDirection(Direction.Bottom)
            .setDuration(Duration.Normal.duration)
            .setInterpolator(DecelerateInterpolator())
            .build()
        val cardStackLayoutManager = CardStackLayoutManager(requireContext())
        cardStackLayoutManager.setRewindAnimationSetting(setting)
        cardStackLayoutManager.setStackFrom(StackFrom.None)

        return cardStackLayoutManager
    }


    private fun observeStates() {
        lifecycleScope {
            homeViewModel.viewStateLoading.collectLatest {
                onLoading(it)
            }
        }
        lifecycleScope {
            homeViewModel.viewStateError.collectLatest {
                handleResponseError(it)
            }
        }

        lifecycleScope {
            homeViewModel.viewStateArticles.collect { articles ->
                articlesAdapter.setList(articles)
            }
        }

        lifecycleScope {
            homeViewModel.viewStateFavouriteStatusArticle.observe(viewLifecycleOwner) { favouriteStatusArticle ->
                handleItemUI(favouriteStatusArticle)
            }
        }

        lifecycleScope {
            homeViewModel.viewStateMedications.collect { schedules ->
                schedulesAdapter.submitList(schedules)
            }
        }
    }

    private fun handleItemUI(favouriteStatusArticle: Pair<Article, Boolean>?) {
        favouriteStatusArticle?.let {
            val (article, isFavorite) = favouriteStatusArticle
            articlesAdapter.changeFavouriteStatusArticle(article, isFavorite)
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

    private fun getArticles(searchQuery: String) {
        val queryTrimmed = searchQuery.trim()
        if (searchQuery.isEmpty())
            homeViewModel.getArticles()
        else if(queryTrimmed.isNotEmpty())
            homeViewModel.getArticles(queryTrimmed)
    }

}