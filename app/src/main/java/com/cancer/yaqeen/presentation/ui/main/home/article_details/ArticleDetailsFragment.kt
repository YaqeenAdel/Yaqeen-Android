package com.cancer.yaqeen.presentation.ui.main.home.article_details

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentArticleDetailsBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.util.MyWebViewClient
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindResourceImage
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ArticleDetailsFragment : BaseFragment() {

    private var binding: FragmentArticleDetailsBinding by autoCleared()

    private lateinit var navController: NavController

    private val args: ArticleDetailsFragmentArgs by navArgs()

    private val article by lazy {
        args.article
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
    }

    private fun setListener(){
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.ivArticleBookmark.setOnClickListener {

        }
        binding.ivShare.setOnClickListener {

        }
    }
    private fun setArticleDetails() {
        article.run {
            binding.tvArticleHeadline.text = title
            binding.tvInterestName.text = interests.firstOrNull()?.interestName
            binding.cardCategory.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(interests.firstOrNull()?.backgroundColor))
            binding.tvInterestName.setTextColor(ColorStateList.valueOf(Color.parseColor(interests.firstOrNull()?.textColor)))
            binding.tvArticleWriter.text = "by/ $authorUserID"

            bindResourceImage(
                binding.ivArticleBookmark,
                if(isFavorite) R.drawable.ic_bookmark_checked else R.drawable.ic_bookmark_unchecked
            )

            setupWebView(link)
        }
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

}