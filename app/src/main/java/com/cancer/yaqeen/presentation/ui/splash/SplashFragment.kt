package com.cancer.yaqeen.presentation.ui.splash

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.databinding.FragmentSplashBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.enableNotificationPermissions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class SplashFragment : BaseFragment() {

    private var _binding: FragmentSplashBinding by autoCleared()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val isLogged = splashViewModel.userIsLogged()

        Handler().postDelayed({
            if(isLogged){
                navController.navigate(
                    SplashFragmentDirections.actionSplashFragmentToQuoteFragment()
                )
            }else {
                navController.navigate(
                    SplashFragmentDirections.actionSplashFragmentToOnBoardingFragment()
                )
            }
        }, 3000)
    }

}