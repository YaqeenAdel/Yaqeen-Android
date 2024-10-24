package com.cancer.yaqeen.presentation.ui

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.auth0.android.Auth0
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.databinding.ActivityMainBinding
import com.cancer.yaqeen.databinding.ProgressBarBinding
import com.cancer.yaqeen.presentation.util.MyContextWrapper
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.enableDrawOverlaysPermission
import com.cancer.yaqeen.presentation.util.updateConfiguration
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject
import kotlin.system.exitProcess


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var progressBarBinding: ProgressBarBinding

    @Inject
    lateinit var prefEncryptionUtil: SharedPrefEncryptionUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateConfiguration(prefEncryptionUtil.selectedLanguage, resources, this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navController = findNavController(R.id.activity_main_nav_host_fragment)

        progressBarBinding = binding.progressBar

        val navView: BottomNavigationView = binding.navView

        val account = Auth0(getString(R.string.com_auth0_client_id), getString(R.string.com_auth0_domain))

        val intentAction: String? = intent?.action
        val intentData: Uri? = intent?.data

        navView.setupWithNavController(navController)
    }

    fun displayBottomMenu(show: Boolean) =
        binding.navView.changeVisibility(show, true)

    fun displayLoading(show: Boolean) {
        progressBarBinding.progress.changeVisibility(show, true)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // must store the new intent unless getIntent()
        // will return the old one
        setIntent(intent)
        val action: String? = intent?.action
        val data: Uri? = intent?.data
    }

    fun changeLanguageByDestination(destId: Int){
        lifecycleScope.launch {
            delay(100)
           // setlanguage("ar")
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            val navBuilder = NavDeepLinkBuilder(this@MainActivity)
                .setGraph(R.navigation.nav_graph)
                .setDestination(destId)
                .setComponentName(MainActivity::class.java)

            val pendingIntent = navBuilder.createTaskStackBuilder()
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)!!

            finishAffinity()
            pendingIntent.send(this@MainActivity, 0, intent)
            exitProcess(0)
        }
    }

    fun setlanguage(language: String){
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = getResources()
        val configuration = resources.getConfiguration()
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.getDisplayMetrics())
    }
    fun changeLanguage(){
        lifecycleScope.launch {
            delay(100)
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            finishAffinity()
            startActivity(intent)
            exitProcess(0)
        }
    }
}