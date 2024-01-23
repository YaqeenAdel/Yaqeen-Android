package com.cancer.yaqeen.presentation.ui.auth.intro.user_type

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentSelectUserTypeBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.auth.OnboardingViewModel
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SelectUserTypeFragment : BaseFragment() {


    private var binding: FragmentSelectUserTypeBinding by autoCleared()

    private lateinit var navController: NavController

    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSelectUserTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        updateUI()

        setListener()

        observeStates()
    }

    private fun updateUI() {
        changeCircleColorOfRadioButtons()

//        viewModel.getUserProfile()?.apply {
//            val isDoctor = userType == UserType.DOCTOR
//            binding.btnPatient.isChecked = !isDoctor
//            binding.btnDoctor.isChecked = isDoctor
//        }

        val spannable = SpannableStringBuilder("1/4")
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary_color)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvPageNumber.text = spannable

    }

    private fun setListener(){
        binding.btnPrevious.setOnClickListener {
            navController.tryPopBackStack()
        }

        binding.btnNext.setOnClickListener {
            val isPatient = binding.btnPatient.isChecked
            viewModel.selectUser(isPatient)

            viewModel.updateUserProfile()
        }

        binding.btnPatient.setOnClickListener {
            selectUser(true)
        }

        binding.btnDoctor.setOnClickListener {
            selectUser(false)
        }


        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
    }

    private fun selectUser(isPatient: Boolean){
        viewModel.selectUser(isPatient)

        viewModel.updateUserProfile()
    }

    private fun changeCircleColorOfRadioButtons() {
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked), // unchecked
                intArrayOf(android.R.attr.state_checked)    // checked
            ),
            intArrayOf(
                ContextCompat.getColor(requireContext(), R.color.light_black), // unchecked
                ContextCompat.getColor(requireContext(), R.color.primary_color) // checked
            )
        )

// Apply color state list to the radio button
        binding.btnPatient.buttonTintList = colorStateList
        binding.btnDoctor.buttonTintList = colorStateList
    }

    private fun observeStates() {
        lifecycleScope {
            viewModel.viewStateLoading.collectLatest {
                onLoading(it)
            }
        }
        lifecycleScope {
            viewModel.viewStateError.collectLatest {
                handleResponseError(it)
            }
        }
        lifecycleScope {
            viewModel.viewStateUpdateProfileSuccess.collectLatest {
                it?.let {
                    val isPatient = binding.btnPatient.isChecked
                    viewModel.selectUser(isPatient)

                    if(isPatient)
                        navController.tryNavigate(
                            SelectUserTypeFragmentDirections.actionSelectUserTypeFragmentToSelectCancerTypeFragment()
                        )
                    else
                        navController.tryNavigate(
                            SelectUserTypeFragmentDirections.actionSelectUserTypeFragmentToSpecializationFragment()
                        )
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