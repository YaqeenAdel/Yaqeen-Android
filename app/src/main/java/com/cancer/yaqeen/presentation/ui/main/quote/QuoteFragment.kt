package com.cancer.yaqeen.presentation.ui.main.quote

import android.os.Bundle
import kotlin.random.Random
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentQuoteBinding
import com.cancer.yaqeen.databinding.FragmentSplashBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.service.ReminderWorker
import com.cancer.yaqeen.presentation.ui.auth.OnBoardingFragmentDirections
import com.cancer.yaqeen.presentation.ui.splash.SplashViewModel
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindResourceImage
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class QuoteFragment : BaseFragment() {

    private var _binding: FragmentQuoteBinding by autoCleared()

    private lateinit var navController: NavController

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentQuoteBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()


        _binding.btnClose.setOnClickListener {
            navController.tryNavigate(
                QuoteFragmentDirections.actionQuoteFragmentToHomeFragment()

            )
        }

        viewModel.refreshToken(requireContext())

        displayRandomView()

//        createWorkRequest("Test", 5)
    }

    private fun displayRandomView() {
        var colorId: Int
        var backgroundId: Int
        var quoteTitle: String
        var quoteOwner: String
        when(getRandomIntInRange(0, Constants.MAX_QUOTES)){
            0 -> {
                colorId = R.color.white
                backgroundId = R.drawable.background_gradient_quote_view
                quoteTitle = getString(R.string.quote_title_1)
                quoteOwner = getString(R.string.quote_owner_1)
            }
            else -> {
                colorId = R.color.black
                backgroundId = R.drawable.background_gradient_quote_view2
                quoteTitle = getString(R.string.quote_title_2)
                quoteOwner = getString(R.string.quote_owner_2)
            }
        }
        bindResourceImage(_binding.ivBackground, backgroundId)
        _binding.tvQuoteTitle.setTextColor(ContextCompat.getColor(requireContext(), colorId))
        _binding.tvQuoteOwner.setTextColor(ContextCompat.getColor(requireContext(), colorId))
        _binding.tvQuoteTitle.text = quoteTitle
        _binding.tvQuoteOwner.text = quoteOwner
    }

    private fun getRandomIntInRange(startInclusive: Int, endExclusive: Int): Int {
        require(startInclusive < endExclusive) { "Invalid range" }
        return Random.nextInt(startInclusive, endExclusive)
    }

    private fun createWorkRequest(message: String,timeDelayInSeconds: Long) {
        val myWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(timeDelayInSeconds, TimeUnit.SECONDS)
            .setInputData(workDataOf(
                "title" to "Reminder",
                "message" to message,
            )
            )
            .build()

        WorkManager.getInstance(requireContext()).enqueue(myWorkRequest)
    }
}