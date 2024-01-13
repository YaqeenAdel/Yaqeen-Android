package com.cancer.yaqeen.presentation.ui.home.article_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentArticleDetailsBinding
import com.cancer.yaqeen.databinding.FragmentHomeBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.util.MyWebViewClient
import com.cancer.yaqeen.presentation.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ArticleDetailsFragment : BaseFragment(), View.OnClickListener {

    private var binding: FragmentArticleDetailsBinding by autoCleared()

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

        val webSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true

        onLoading(true)
        val webViewClient = MyWebViewClient {
            onLoading(it)
        }
        binding.webView.webViewClient = webViewClient

        binding.webView.loadUrl("https://www.webmd.com/diabetes/diabetes-types-insulin")
    }

    override fun onClick(v: View?) {

    }
}