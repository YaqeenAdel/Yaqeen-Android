package com.cancer.yaqeen.presentation.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.home.models.Article
import com.cancer.yaqeen.data.features.home.models.Time
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.data.utils.getTodayDate
import com.cancer.yaqeen.databinding.FragmentHomeBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.home.articles.ArticlesAdapter
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.detectLanguage
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class HomeFragment : BaseFragment(showBottomMenu = true), OnClickListener {

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

        setListener()


        homeViewModel.getBookmarkedArticles()
    }

    override fun onResume() {
        super.onResume()

        binding.tvCurrentDayDate.text = getTodayDate()

        getArticles(binding.editTextSearch.text.toString())
        homeViewModel.getUserInfo()
    }

    private fun setListener(){
        binding.editTextSearch.doOnTextChanged { text, _, _, _ ->
            getArticles(text.toString())
        }
    }

    private fun setupAdapters() {
        setupArticlesAdapter()
        setupTimesAdapter()
    }

    private fun setupArticlesAdapter() {
        articlesAdapter = ArticlesAdapter(
            onItemClick = {
                navController.tryNavigate(
                    HomeFragmentDirections.actionHomeFragmentToArticleDetailsFragment(it)
                )
            },
            onFavouriteArticleClick = {
                if (homeViewModel.userIsLoggedIn())
                    homeViewModel.changeFavouriteStatusArticle(it)
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

    private fun setupTimesAdapter() {
        timesAdapter = TimesAdapter {

        }
        binding.rvTimes.apply {
            adapter = timesAdapter
        }

        timesAdapter.submitList(
            listOf(
                Time(0, "00:00"),
                Time(1, "1:00"),
                Time(2, "2:00"),
                Time(3, "3:00"),
                Time(4, "4:00"),
                Time(5, "5:00"),
                Time(6, "6:00"),
                Time(7, "7:00"),
                Time(8, "8:00"),
                Time(9, "9:00"),
                Time(10, "10:00"),
                Time(11, "11:00"),
                Time(12, "12:00"),
                Time(13, "13:00"),
                Time(14, "14:00"),
                Time(15, "15:00"),
                Time(16, "16:00"),
                Time(17, "17:00"),
                Time(18, "18:00"),
                Time(19, "19:00"),
                Time(20, "20:00"),
                Time(21, "21:00"),
                Time(22, "22:00"),
                Time(23, "23:00")
            )
        )

        selectItem(5)
    }

    private fun selectItem(itemId: Int) {
        val selectItemPosition = timesAdapter.selectItem(itemId)
        if(selectItemPosition >= 2)
            binding.rvTimes.scrollToPosition(selectItemPosition - 2)
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
            homeViewModel.viewStateUser.collect { userInfo ->
                handleUI(userInfo)
            }
        }

        lifecycleScope {
            homeViewModel.viewStateFavouriteStatusArticle.observe(viewLifecycleOwner) { favouriteStatusArticle ->
                handleItemUI(favouriteStatusArticle)
            }
        }
    }

    private fun handleItemUI(favouriteStatusArticle: Pair<Article, Boolean>?) {
        favouriteStatusArticle?.let {
            val (article, isFavorite) = favouriteStatusArticle
            articlesAdapter.changeFavouriteStatusArticle(article, isFavorite)
        }
    }

    private fun handleUI(userInfo: Pair<User?, Boolean>) {
        val (user, isLogged) = userInfo

        binding.groupProfile.changeVisibility(show = isLogged, isGone = false)
        binding.groupGuest.changeVisibility(show = !isLogged, isGone = false)


        binding.tvNameUser.text = user?.name ?: ""
        bindImage(binding.ivProfilePic, user?.pictureURL)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_see_all_calender -> {}
        }
    }

}