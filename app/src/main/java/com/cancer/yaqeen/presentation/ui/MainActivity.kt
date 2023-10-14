package com.cancer.yaqeen.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.databinding.DataBindingUtil
import com.auth0.android.Auth0
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.ActivityMainBinding
import com.cancer.yaqeen.databinding.ProgressBarBinding
import com.cancer.yaqeen.presentation.util.changeVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var progressBarBinding: ProgressBarBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        progressBarBinding = binding.progressBar

        val account = Auth0(getString(R.string.com_auth0_client_id), getString(R.string.com_auth0_domain))

    }

    fun displayLoading(show: Boolean) {
        progressBarBinding.progress.changeVisibility(show, true)
    }
}