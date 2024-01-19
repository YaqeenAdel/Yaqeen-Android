package com.cancer.yaqeen.presentation.ui.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentOnBoardingBinding
import com.cancer.yaqeen.databinding.ItemViewPagerBinding
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage

class PageFragment : Fragment(){

    private var binding: ItemViewPagerBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = ItemViewPagerBinding.inflate(inflater, container, false)
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.apply {
            bindImage(image, arguments?.getString("photoURL"))
            tvTitle.text = "Explore trusted articles to learn more about your condition"
            tvInfo.text = " Discover helpful tips for recovery, Empower your healing journey with reliable information and practical advice."
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}