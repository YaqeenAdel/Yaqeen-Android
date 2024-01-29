package com.cancer.yaqeen.presentation.ui.main.more

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.databinding.FragmentHomeBinding
import com.cancer.yaqeen.databinding.FragmentMoreBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.MainActivity
import com.cancer.yaqeen.presentation.ui.main.home.HomeViewModel
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MoreFragment : BaseFragment(showBottomMenu = true), View.OnClickListener {

    private var binding: FragmentMoreBinding by autoCleared()

    private lateinit var navController: NavController

    private val moreViewModel: MoreViewModel by viewModels()

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

        observeStates()
        setListener()

    }

    override fun onResume() {
        super.onResume()

        moreViewModel.getUserInfo()

        val language = if (moreViewModel.selectedLanguageIsEnglish()){
            getString(R.string.english)
        } else {
            getString(R.string.arabic)
        }
        binding.btnLanguage.text = language
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
            moreViewModel.viewStateUser.collect { userInfo ->
                handleUI(userInfo)
            }
        }
    }

    private fun handleUI(userInfo: Pair<User?, Boolean>) {
        val (user, isLogged) = userInfo

        binding.groupProfile.changeVisibility(show = isLogged, isGone = true)
        binding.groupGuest.changeVisibility(show = !isLogged, isGone = true)

        binding.btnSavedArticles.updateButtonUI(isLogged)
        binding.ivSavedArticles.updateButtonUI(isLogged)
        binding.viewSavedArticles.updateButtonUI(isLogged)
        binding.btnLogOut.updateButtonUI(isLogged)
        binding.ivLogOut.updateButtonUI(isLogged)
        binding.viewLogOut.updateButtonUI(isLogged)

        binding.tvNameUser.text = user?.name ?: ""
        binding.tvEmailUser.text = user?.email ?: ""
        bindImage(binding.ivProfilePic, user?.pictureURL)
    }

    private fun View.updateButtonUI(isLogged: Boolean) {
        isEnabled = isLogged
        alpha = if (isLogged) 1.0f else 0.5f
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_saved_articles -> {
                navController.tryNavigate(MoreFragmentDirections.actionMoreFragmentToSavedArticlesFragment())
            }
            R.id.btn_language -> {
                moreViewModel.switchLanguage()
                (requireActivity() as? MainActivity)?.changeLanguageByDestination(R.id.moreFragment)
            }
            R.id.btn_log_out -> {
                moreViewModel.logOut()
                navController.tryNavigate(MoreFragmentDirections.actionMoreFragmentToOnBoardingFragment())
            }
        }
    }
}