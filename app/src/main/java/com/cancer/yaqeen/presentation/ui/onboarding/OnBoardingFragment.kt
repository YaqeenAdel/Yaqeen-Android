package com.cancer.yaqeen.presentation.ui.onboarding

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.onboarding.models.Photo
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentOnBoardingBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class OnBoardingFragment : BaseFragment(), OnClickListener {

    private var binding: FragmentOnBoardingBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var adapter: ViewPagerAdapter

    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

    private val handler = Handler()
    private var scrollPosition = 0

    private val runnable = object : Runnable {

        override fun run() {
            val count = adapter.itemCount
            binding.viewPager.setCurrentItem(scrollPosition++ % count, true)

            handler.postDelayed(this, 3000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()

        getResourcesData()

        observeStates()
    }

    private fun getResourcesData(){
        onboardingViewModel.getResources()
    }

    private fun observeStates() {
        lifecycleScope {
            onboardingViewModel.viewStateLoading.collectLatest {
                onLoading(it)
            }
        }
        lifecycleScope {
            onboardingViewModel.viewStateError.collectLatest {
                handleResponseError(it)
            }
        }
        lifecycleScope {
            onboardingViewModel.viewStateResources.collectLatest {
                it?.let {
                    setupViewPager(it.photos)
                }
                val user = onboardingViewModel.viewStateLoginSuccess.replayCache
                if(user.isNotEmpty() && user.firstOrNull() != null) {
                    navigateToUpdateProfile()
                }
            }
        }
        lifecycleScope {
            onboardingViewModel.viewStateLoginSuccess.collectLatest {
                Log.d("TAG", "observeStates: $it")
                it?.let {
                    val resources = onboardingViewModel.viewStateResources.replayCache
                    if(resources.isNotEmpty() && resources.firstOrNull() != null) {
                        navigateToUpdateProfile()
                    }else{
//                        getResourcesData()
                    }
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

    private fun setListener() {
        binding.tvLanguage.setOnClickListener(this)
        binding.btnExploreApp.setOnClickListener(this)
        binding.btnJoin.setOnClickListener(this)
        binding.tvLogin.setOnClickListener(this)

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    binding.viewPager.currentItem = tab.position
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })
    }

    private fun setupViewPager(photos: List<Photo>) {
        if(::adapter.isInitialized)
            return

        val pages = mutableListOf<Fragment>()

        photos.onEach {
            pages.add(PageFragment().apply {
                arguments = bundleOf("photoURL" to it.photoURL)
            })
            binding.tabLayout.apply {
                addTab(newTab())
            }
        }

        adapter =
            ViewPagerAdapter(requireActivity().supportFragmentManager, lifecycle, pages, photos)

        binding.viewPager.apply {
            this.adapter = this@OnBoardingFragment.adapter
            clipToPadding = false
        }

        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                scrollPosition = position + 1
            }
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}
        })

        handler.post(runnable)
    }

    private fun navigateToUpdateProfile() {
        removeCallbacks()
        navController.tryNavigate(
            OnBoardingFragmentDirections.actionOnBoardingFragmentToIntroFragment()
        )
    }

    private fun removeCallbacks(){
        binding.viewPager.removeCallbacks(runnable)
        handler.removeCallbacks(runnable)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.tv_language -> {}
            R.id.btn_explore_app -> {}
            R.id.btn_join -> {
//                navigateToUpdateProfile()
            }
            R.id.tv_login -> {
                removeCallbacks()
                onboardingViewModel.login(requireContext())
            }
        }
    }

}