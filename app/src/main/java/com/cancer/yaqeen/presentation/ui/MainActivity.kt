package com.cancer.yaqeen.presentation.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.auth0.android.Auth0
import com.cancer.yaqeen.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val account = Auth0(getString(R.string.com_auth0_client_id), getString(R.string.com_auth0_domain))

    }
}