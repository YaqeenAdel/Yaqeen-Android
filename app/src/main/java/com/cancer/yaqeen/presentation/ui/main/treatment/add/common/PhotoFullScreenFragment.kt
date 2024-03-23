package com.cancer.yaqeen.presentation.ui.main.treatment.add.common

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentCalendarBinding
import com.cancer.yaqeen.databinding.FragmentPhotoFullScreenBinding
import com.cancer.yaqeen.presentation.ui.main.treatment.add.symptoms.SymptomsTypesFragmentArgs
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.tryPopBackStack

class PhotoFullScreenFragment : Fragment() {

    private var binding: FragmentPhotoFullScreenBinding by autoCleared()

    private lateinit var navController: NavController

    private val args: PhotoFullScreenFragmentArgs by navArgs()

    private val imageUri by lazy {
        args.imageuri
    }

    private val imageUrl by lazy {
        args.imageurl
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPhotoFullScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.ivClose.setOnClickListener {
            navController.tryPopBackStack()
        }


        imageUri?.let {
            updateUI(Uri.parse(imageUri))
        }
        imageUrl?.let {
            updateUI(imageUrl)
        }

        val observer = binding.ivPhoto.viewTreeObserver

        observer.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Remove the listener to prevent multiple calls to this method
                binding.ivPhoto.viewTreeObserver.removeOnGlobalLayoutListener(this)
                // Get the width of the ViewPager
                val width = binding.ivPhoto.width
                // Set the height of the ViewPager to be the same as its width
                binding.ivPhoto.layoutParams.height = width
                // Request a new layout to update the ViewPager's height
                binding.ivPhoto.requestLayout()
            }
        })
    }

    private fun updateUI(uri: Uri?) {
        binding.ivPhoto.setImageURI(uri)

    }
    private fun updateUI(url: String?) {
        bindImage(binding.ivPhoto, url)
    }
}