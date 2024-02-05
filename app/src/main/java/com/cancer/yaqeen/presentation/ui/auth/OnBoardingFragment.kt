package com.cancer.yaqeen.presentation.ui.auth

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.onboarding.models.OnBoardingInfo
import com.cancer.yaqeen.data.features.onboarding.models.Photo
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentNewOnBoardingBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.util.Constants.BODY_KEY
import com.cancer.yaqeen.presentation.util.Constants.PHOTO_ID_KEY
import com.cancer.yaqeen.presentation.util.Constants.TITLE_KEY
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class OnBoardingFragment : BaseFragment(), OnClickListener {

    private var binding: FragmentNewOnBoardingBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var adapter: ViewPagerAdapter

    private val onboardingViewModel: OnboardingViewModel by activityViewModels()

    private val handler = Handler()
    private var scrollPosition = 0

    private val runnable = object : Runnable {

        override fun run() {
            try {
                val count = adapter.itemCount
                binding.viewPager.setCurrentItem(scrollPosition++ % count, true)

                handler.postDelayed(this, 3000)
            } catch (_: Exception) {
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                removeCallbacks()
                requireActivity().finish()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNewOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()

        getResourcesData()

        observeStates()

        setupViewPager()

//        setupLanguageAutoCompleteAdapter()
    }

    private fun getResourcesData() {
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
                if (user.isNotEmpty() && user.firstOrNull() != null) {
                    navigateToUpdateProfile()
                }
            }
        }
        lifecycleScope {
            onboardingViewModel.viewStateLoginSuccess.collectLatest {
                it?.let {
                    val resources = onboardingViewModel.viewStateResources.replayCache
                    if (resources.isNotEmpty() && resources.firstOrNull() != null) {
//                        Toast.makeText(context,
//                            getString(R.string.you_have_logged_in_successfully),Toast.LENGTH_SHORT).show()
                        navigateToUpdateProfile()
                    } else {
//                        getResourcesData()
                    }
                }

            }
        }
        lifecycleScope {
            onboardingViewModel.viewStateUserDataCompleted.observe(viewLifecycleOwner) { response ->
                if (response == true) navigateToHome()
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
        binding.tvSkip.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
        binding.btnSignup.setOnClickListener(this)

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

//    private fun setupLanguageAutoCompleteAdapter() {
//        ArrayAdapter.createFromResource(
//            requireContext(),
//            R.array.languages_array,
//            R.layout.simple_spinner_item
//        ).also { adapter ->
//            // Specify the layout to use when the list of choices appears.
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//            // Apply the adapter to the spinner.
//            binding.spinnerLanguage.adapter = adapter
//
//        }
//        binding.spinnerLanguage.setSelection(
//            if (onboardingViewModel.selectedLanguageIsEnglish()) 0
//            else 1
//        )
//
//        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                val isSameLanguage = onboardingViewModel.changeLanguage(
//                    if (position == 0)
//                        Language.ENGLISH.lang
//                    else Language.ARABIC.lang
//                )
//                if(!isSameLanguage) {
////                    removeCallbacks()
//                    (requireActivity() as? MainActivity)?.changeLanguageByDestination(R.id.onBoardingFragment)
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }
//    }

    private fun setupViewPager(photos: List<Photo>? = null) {
        val onBoardingList: List<OnBoardingInfo> = listOf(
            OnBoardingInfo(
                photoId = R.drawable.ic_screen_1,
                title = getString(R.string.on_boarding_title_1),
                body = getString(R.string.on_boarding_body_1)
            ),
            OnBoardingInfo(
                photoId = R.mipmap.ic_screen_2,
                title = getString(R.string.on_boarding_title_2),
                body = getString(R.string.on_boarding_body_2)
            )
        )
        val pages = mutableListOf<Fragment>()

        binding.tabLayout.removeAllTabs()
        onBoardingList.onEach {
            pages.add(PageFragment().apply {
                arguments =
                    bundleOf(PHOTO_ID_KEY to it.photoId, TITLE_KEY to it.title, BODY_KEY to it.body)
            })
            binding.tabLayout.apply {
                addTab(newTab())
            }
        }
        adapter =
            ViewPagerAdapter(childFragmentManager, lifecycle, pages)

        binding.viewPager.apply {
            this.adapter = this@OnBoardingFragment.adapter
            clipToPadding = false
        }

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                scrollPosition = position + 1
            }

            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }
        })

        handler.post(runnable)
    }

    private fun navigateToHome() {
        removeCallbacks()
        navController.tryNavigate(
            OnBoardingFragmentDirections.actionOnBoardingFragmentToQuoteFragment()

        )
    }

    private fun navigateToUpdateProfile() {
        removeCallbacks()
        navController.tryNavigate(
            OnBoardingFragmentDirections.actionOnBoardingFragmentToTermsAndConditionFragment()
        )
    }

    private fun removeCallbacks() {
        binding.viewPager.removeCallbacks(runnable)
        handler.removeCallbacks(runnable)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_skip -> {
                navigateToHome()
            }

            R.id.btn_signup, R.id.btn_login -> {
                removeCallbacks()
                onboardingViewModel.login(requireContext())
            }
        }
    }

}