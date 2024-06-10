package com.cancer.yaqeen.presentation.ui.main.required_auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentAuthBinding
import com.cancer.yaqeen.databinding.FragmentCalendarBinding
import com.cancer.yaqeen.presentation.base.BaseBottomSheetDialogFragment
import com.cancer.yaqeen.presentation.ui.auth.OnBoardingFragmentDirections
import com.cancer.yaqeen.presentation.ui.auth.OnboardingViewModel
import com.cancer.yaqeen.presentation.util.Constants.REQUEST_USER_LOG_IN_KEY
import com.cancer.yaqeen.presentation.util.Constants.USER_LOG_IN_KEY
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryNavigateUp
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthFragment : BaseBottomSheetDialogFragment(showBottomMenu = true) {

    private var binding: FragmentAuthBinding by autoCleared()

    private lateinit var navController: NavController

    private val onboardingViewModel: OnboardingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()

        observeStates()
    }

    private fun setListener() {
        binding.btnLogin.setOnClickListener {
            onboardingViewModel.login(requireContext())
        }
        binding.btnSignup.setOnClickListener {
            onboardingViewModel.login(requireContext())
        }

    }

    private fun observeStates() {
        lifecycleScope {
            onboardingViewModel.viewStateLoading.collectLatest {
//                onLoading(it)
            }
        }
        lifecycleScope {
            onboardingViewModel.viewStateError.collectLatest {
                handleResponseError(it)
            }
        }
        lifecycleScope {
            onboardingViewModel.viewStateLoginSuccess.collectLatest {
                it?.let {
                    navController.tryNavigate(
                        AuthFragmentDirections.actionAuthFragmentToTermsAndConditionFragment()
                    )
                }

            }
        }
        lifecycleScope {
            onboardingViewModel.viewStateUserDataCompleted.observe(viewLifecycleOwner) { response ->
                if (response == true) {
                    setFragmentResult(REQUEST_USER_LOG_IN_KEY, bundleOf(USER_LOG_IN_KEY to true))
                    navController.tryNavigateUp()
                }
            }
        }
    }

    private fun handleResponseError(errorEntity: ErrorEntity?) {
        val errorMessage = handleError(errorEntity)
        displayErrorMessage(errorMessage)
    }

    private fun displayErrorMessage(errorMessage: String?) {
        errorMessage?.let {
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}