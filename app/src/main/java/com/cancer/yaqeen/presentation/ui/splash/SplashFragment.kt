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

    private val requestPermissionLauncher: ActivityResultLauncher<String?> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted == true)
            checkUserInfo()
    }

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

        observeUiState()
        checkNotificationPermission()
    }

    private fun observeUiState() {
        lifecycleScope {
            splashViewModel.viewStateUserInfo.collectLatest { isLogged ->
                if (isLogged == null){
                    Handler().postDelayed({}, 3000)
                }else if(isLogged){
                    navController.navigate(
                        SplashFragmentDirections.actionSplashFragmentToQuoteFragment()
                    )
                }else {
                    navController.navigate(
                        SplashFragmentDirections.actionSplashFragmentToOnBoardingFragment()
                    )
                }
            }
        }
    }
    private fun checkNotificationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (PermissionChecker.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PermissionChecker.PERMISSION_GRANTED){
                checkUserInfo()
            } else {
                enableNotificationPermissions(
                    requestPermissionLauncher
                )
            }
        }else {
            checkUserInfo()
        }
    }

    private fun checkUserInfo(){
        splashViewModel.checkUserInfo()
    }

}