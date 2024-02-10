package com.cancer.yaqeen.presentation.ui.main.search

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.articles.models.Article
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentArticlesBinding
import com.cancer.yaqeen.databinding.FragmentHomeBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.home.HomeFragmentDirections
import com.cancer.yaqeen.presentation.ui.main.home.HomeViewModel
import com.cancer.yaqeen.presentation.ui.main.home.articles.ArticlesAdapter
import com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.MedicationsFragmentArgs
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class ArticlesFragment : BaseFragment() {

    private var binding: FragmentArticlesBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var articlesAdapter: ArticlesAdapter

    private val homeViewModel: HomeViewModel by viewModels()

    private val args: ArticlesFragmentArgs by navArgs()

    private val interestName by lazy {
        args.interestName
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentArticlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setupArticlesAdapter()

        observeStates()

        setListener()
//
//        homeViewModel.getBookmarkedArticles()

        binding.tvNameArticles.text = getString(R.string.name_articles, interestName)

        homeViewModel.getArticles(interestName)
    }

    private fun setListener(){
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
    }

    private fun setupArticlesAdapter() {
        articlesAdapter = ArticlesAdapter(
            onItemClick = {
                navController.tryNavigate(
                    ArticlesFragmentDirections.actionArticlesFragmentToArticleDetailsFragment(it, false)
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
}