package com.cancer.yaqeen.presentation.ui.main.home.article_details

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
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
import com.cancer.yaqeen.databinding.FragmentArticleDetailsBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.home.HomeViewModel
import com.cancer.yaqeen.presentation.util.MyWebViewClient
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindResourceImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class ArticleDetailsFragment : BaseFragment() {

    private var binding: FragmentArticleDetailsBinding by autoCleared()

    private lateinit var navController: NavController

    private val homeViewModel: HomeViewModel by viewModels()

    private val args: ArticleDetailsFragmentArgs by navArgs()

    private val article by lazy {
        args.article
    }

    private val isSaved by lazy {
        args.isSaved
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentArticleDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setArticleDetails()

        setListener()

        observeStates()
    }

    private fun setListener(){
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.ivArticleBookmark.setOnClickListener {
            if (homeViewModel.userIsLoggedIn())
                homeViewModel.changeFavouriteStatusArticle(article)
        }
        binding.ivShare.setOnClickListener {

        }
    }

    private fun setArticleDetails() {
        with(article) {
            createUnderLine()
            binding.tvArticleHeadline.text = title
            binding.tvInterestName.text = interests.firstOrNull()?.interestName
            binding.tvArticleLink.text = link
            interests.firstOrNull()?.backgroundColor?.let {
                binding.ivInterestIcon.imageTintList = ColorStateList.valueOf(Color.parseColor(it))
            }

            binding.tvArticleWriter.text = getString(R.string.by, "")

            bindResourceImage(
                binding.ivArticleBookmark,
                if(isFavorite || isSaved) R.drawable.ic_bookmark_checked else R.drawable.ic_bookmark_unchecked
            )

            setupWebView(link)
        }
    }

    private fun createUnderLine(){
        val paint = Paint()

        paint.color = resources.getColor(android.R.color.black)

        paint.style = Paint.Style.STROKE

        paint.strokeWidth = 2f

        binding.tvArticleLink.paintFlags = binding.tvArticleLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        binding.tvArticleLink.setTextColor(resources.getColor(android.R.color.black))

        binding.tvArticleLink.setLayerType(View.LAYER_TYPE_SOFTWARE, paint)
    }

    private fun setupWebView(link: String) {
        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true

        onLoading(true)
        val webViewClient = MyWebViewClient {
            onLoading(it)
        }
        binding.webView.webViewClient = webViewClient

        binding.webView.loadUrl(link)
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
            homeViewModel.viewStateFavouriteStatusArticle.observe(viewLifecycleOwner) { favouriteStatusArticle ->
                handleItemUI(favouriteStatusArticle)
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

    private fun handleItemUI(favouriteStatusArticle: Pair<Article, Boolean>?) {
        favouriteStatusArticle?.let {
            val (_, isFavorite) = favouriteStatusArticle
            article.isFavorite = isFavorite

            bindResourceImage(
                binding.ivArticleBookmark,
                if(isFavorite) R.drawable.ic_bookmark_checked else R.drawable.ic_bookmark_unchecked
            )
        }
    }
}