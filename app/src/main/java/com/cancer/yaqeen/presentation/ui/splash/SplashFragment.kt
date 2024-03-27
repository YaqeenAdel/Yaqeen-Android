package com.cancer.yaqeen.presentation.ui.splash

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.PermissionChecker
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.databinding.FragmentSplashBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cronutils.builder.CronBuilder
import com.cronutils.model.Cron
import com.cronutils.model.CronType
import com.cronutils.model.definition.CronDefinitionBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import com.cronutils.model.field.expression.FieldExpressionFactory.*


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
//        createCronExpression()
    }

    private fun observeUiState() {
        lifecycleScope {
            viewModel.viewStateUserInfo.collectLatest { isLogged ->
                if (isLogged == null){
                    Handler().postDelayed({
                        checkNotificationPermission()
                    }, 3000)
                }else if(isLogged){
//                    viewModel.refreshToken(requireContext())
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

    private fun createCronExpression() {
        // Define your own cron: arbitrary fields are allowed and last field can be optional
        // Define your own cron: arbitrary fields are allowed and last field can be optional
        var cronDefinition = CronDefinitionBuilder.defineCron()
            .withSeconds().and()
            .withMinutes().and()
            .withHours().and()
            .withDayOfMonth()
            .supportsHash().supportsL().supportsW().and()
            .withMonth().and()
            .withDayOfWeek()
            .withIntMapping(7, 0) //we support non-standard non-zero-based numbers!
            .supportsHash().supportsL().supportsW().and()
            .withYear().optional().and()
            .instance()

// or get a predefined instance

// or get a predefined instance
        cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ)

        val cron: Cron =
            CronBuilder.cron(CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ))
                .withYear(always())
                .withMonth(always())
                .withDoM(questionMark())
                .withDoW(every(2))
                .withHour(on(1))
                .withMinute(on(0))
                .withSecond(on(0))
                .instance()
// Obtain the string expression
// Obtain the string expression
        val cronAsString: String = cron.asString()

        Log.d("TAG", "createCronExpression: $cronAsString")
    }
}