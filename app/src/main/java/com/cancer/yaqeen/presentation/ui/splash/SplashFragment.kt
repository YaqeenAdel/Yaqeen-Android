package com.cancer.yaqeen.presentation.ui.splash

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentSplashBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.util.autoCleared
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : BaseFragment() {

    private var _binding: FragmentSplashBinding by autoCleared()

    private lateinit var navController: NavController

    private val viewModel: SplashViewModel by viewModels()

    private val launcher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
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

    }

    private fun observeUiState() {
        lifecycleScope {
            viewModel.viewStateUserInfo.collectLatest { isLogged ->
                if (isLogged == null){
                    Handler().postDelayed({
                        checkNotificationPermission()
                    }, 3000)
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
        //TODO: Will replace != with ==
        if (PermissionChecker.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) != PermissionChecker.PERMISSION_GRANTED){
            checkUserInfo()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }else {
                checkUserInfo()
            }
        }
    }

    private fun checkUserInfo(){
        viewModel.checkUserInfo()
    }
}