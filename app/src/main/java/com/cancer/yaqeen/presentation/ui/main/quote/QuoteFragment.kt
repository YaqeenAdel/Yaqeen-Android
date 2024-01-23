package com.cancer.yaqeen.presentation.ui.main.quote

import android.os.Bundle
import kotlin.random.Random
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentQuoteBinding
import com.cancer.yaqeen.databinding.FragmentSplashBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.auth.OnBoardingFragmentDirections
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindResourceImage
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class QuoteFragment : BaseFragment() {

    private var _binding: FragmentQuoteBinding by autoCleared()

    private lateinit var navController: NavController

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

        displayRandomView()
    }

    private fun displayRandomView() {
        var colorId = R.color.white
        var backgroundId = R.drawable.background_gradient_quote_view
        var quoteTitle = ""
        var quoteOwner = ""
        when(getRandomIntInRange(0, 2)){
            0 -> {
                colorId = R.color.white
                backgroundId = R.drawable.background_gradient_quote_view
                quoteTitle = "“When we long for life without difficulties, remind us that oaks grow strong in contrary winds, and diamonds are made under pressure.”"
                quoteOwner = "Peter Marshall"
            }
            else -> {
                colorId = R.color.black
                backgroundId = R.drawable.background_gradient_quote_view2
                quoteTitle = "“You have to be willing to give up the life you planned, and instead, greet the life that is waiting for you.”"
                quoteOwner = "Joseph Campbell"
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
}