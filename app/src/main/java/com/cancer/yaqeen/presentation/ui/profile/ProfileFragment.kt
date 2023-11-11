package com.cancer.yaqeen.presentation.ui.profile

import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract.Profile
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.onboarding.models.Photo
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentOnBoardingBinding
import com.cancer.yaqeen.databinding.FragmentProfileBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.onboarding.OnBoardingFragmentDirections
import com.cancer.yaqeen.presentation.ui.onboarding.OnboardingViewModel
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragment : BaseFragment(), OnClickListener {

    private var binding: FragmentProfileBinding by autoCleared()

    private lateinit var navController: NavController


    private val profileViewModel: ProfileViewModel by activityViewModels()

    private val handler = Handler()
    private var scrollPosition = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()

        getProfile()

     }

    private fun getProfile(){
        lifecycleScope.launch {
            profileViewModel.getCurrentUser(requireContext()).collect{ data->
                binding.let {
                    it.emailTV.text=data.data!!.email
                    it.profileName.text=data.data!!.name
                    Glide.with(requireContext())
                        .load(data.data.pictureURL)
                        .circleCrop()
                        .into(it.profileImage);

                }            }

        }
    }




    private fun setListener() {





    }





    override fun onClick(v: View?) {

    }

}