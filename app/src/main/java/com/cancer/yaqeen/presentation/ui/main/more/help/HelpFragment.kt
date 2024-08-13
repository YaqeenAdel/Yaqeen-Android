package com.cancer.yaqeen.presentation.ui.main.more.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.BuildConfig.HELP
import com.cancer.yaqeen.databinding.FragmentHelpBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.util.MyWebViewClient
import com.cancer.yaqeen.presentation.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HelpFragment : BaseFragment() {

    private var binding: FragmentHelpBinding by autoCleared()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()


        setListener()

        setupWebView(HELP)
    }

    private fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
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