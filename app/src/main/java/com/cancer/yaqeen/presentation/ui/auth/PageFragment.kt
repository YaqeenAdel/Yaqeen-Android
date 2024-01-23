package com.cancer.yaqeen.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.cancer.yaqeen.databinding.ItemViewPagerBinding
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
            tvTitle.text = arguments?.getString("title")
            tvBody.text = arguments?.getString("body")
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
    }
}