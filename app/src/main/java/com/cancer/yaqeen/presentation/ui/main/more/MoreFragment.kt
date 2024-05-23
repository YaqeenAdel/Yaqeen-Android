package com.cancer.yaqeen.presentation.ui.main.more

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentMoreBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.service.AlarmReminder
import com.cancer.yaqeen.presentation.service.ReminderManager
import com.cancer.yaqeen.presentation.service.WorkerReminder
import com.cancer.yaqeen.presentation.ui.MainActivity
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.schedulingPermissionsAreGranted
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MoreFragment : BaseFragment(showBottomMenu = true), View.OnClickListener {

    private var binding: FragmentMoreBinding by autoCleared()

    private lateinit var navController: NavController

    private val moreViewModel: MoreViewModel by viewModels()

    private val workerReminder: ReminderManager by lazy {
        AlarmReminder(requireContext())
    }

    private val workerReminderPeriodically: ReminderManager by lazy {
        WorkerReminder(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()

        updateUI()
        observeStates()

        setLanguage()

    }

    override fun onResume() {
        super.onResume()

        updateUI()
    }

    private fun setListener(){
        binding.btnMedicine.setOnClickListener(this)
        binding.btnSymptoms.setOnClickListener(this)
        binding.btnSavedArticles.setOnClickListener(this)
        binding.btnLanguage.setOnClickListener(this)
        binding.btnAccountSetting.setOnClickListener(this)
        binding.btnHelp.setOnClickListener(this)
        binding.btnLogOut.setOnClickListener(this)

    }
    private fun observeStates() {
        lifecycleScope {
            moreViewModel.viewStateLogoutSuccess.observe(viewLifecycleOwner) { response ->
                response?.let { (logoutSuccess, appHasWorker) ->
                    if(logoutSuccess){
                        workerReminder.cancelAllReminders()
                        if (appHasWorker)
                            workerReminderPeriodically.cancelAllReminders()
                        navController.tryNavigate(MoreFragmentDirections.actionMoreFragmentToOnBoardingFragment())
                    }
                }
            }
        }
    }

    private fun updateUI() {
        val isLogged = moreViewModel.userIsLoggedIn()
        val user = moreViewModel.getUser()

        binding.groupProfile.changeVisibility(show = isLogged, isGone = true)
        binding.groupGuest.changeVisibility(show = !isLogged, isGone = true)

        binding.btnSavedArticles.changeVisibility(isLogged, isGone = true)
        binding.ivSavedArticles.changeVisibility(isLogged, isGone = true)
        binding.viewSavedArticles.changeVisibility(isLogged, isGone = true)
        binding.btnLogOut.changeVisibility(isLogged, isGone = true)
        binding.ivLogOut.changeVisibility(isLogged, isGone = true)
        binding.viewLogOut.changeVisibility(isLogged, isGone = true)

        binding.tvNameUser.text = user?.name ?: ""
        binding.tvEmailUser.text = user?.email ?: ""
        bindImage(binding.ivProfilePic, user?.pictureURL)
    }

    private fun setLanguage() {
        val language = if (moreViewModel.selectedLanguageIsEnglish()){
            getString(R.string.arabic)
        } else {
            getString(R.string.english)
        }
        binding.btnLanguage.text = language
    }

    private fun View.updateButtonUI(isLogged: Boolean) {
        isEnabled = isLogged
        alpha = if (isLogged) 1.0f else 0.5f
    }

    private fun navigateToAddingSchedule(onNavigate:() -> Unit) {
        if (moreViewModel.userIsLoggedIn()) {
            if (schedulingPermissionsAreGranted(requireActivity(), requireContext()))
                onNavigate()
        }
        else {
            navController.tryNavigate(R.id.authFragment)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_medicine -> {
                navigateToAddingSchedule {
                    navController.tryNavigate(MoreFragmentDirections.actionMoreFragmentToMedicationsFragment(null))
                }
            }
            R.id.btn_symptoms -> {
                navigateToAddingSchedule {
                    navController.tryNavigate(
                        MoreFragmentDirections.actionMoreFragmentToSymptomsTypesFragment(
                            null
                        )
                    )
                }
            }
            R.id.btn_saved_articles -> {
                navController.tryNavigate(MoreFragmentDirections.actionMoreFragmentToSavedArticlesFragment())
            }
            R.id.btn_language -> {
                moreViewModel.switchLanguage()
                (requireActivity() as? MainActivity)?.changeLanguageByDestination(R.id.moreFragment)
            }
            R.id.btn_help -> {
                navController.tryNavigate(MoreFragmentDirections.actionMoreFragmentToHelpFragment())
            }
            R.id.btn_log_out -> {
                moreViewModel.logout(requireContext())
            }
        }
    }
}