package com.cancer.yaqeen.presentation.ui.main.treatment.add.common

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentMethodAttachedFileBinding
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.cameraPermissionsAreGranted
import com.cancer.yaqeen.presentation.util.enableCameraPermissions
import com.cancer.yaqeen.presentation.util.enableStoragePermissions
import com.cancer.yaqeen.presentation.util.storagePermissionsAreGranted
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryNavigateUp
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MethodAttachedFileFragment : BottomSheetDialogFragment() {

    private var binding: FragmentMethodAttachedFileBinding by autoCleared()

    private lateinit var navController: NavController


    private val requestCameraPermissionLauncher: ActivityResultLauncher<String?> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                openCamera()
            }
        }


//    private val requestCameraMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>?> =
//        registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions()
//        ) { permissions ->
//            if (permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true || permissions[Manifest.permission.READ_MEDIA_IMAGES] == true)
//                if (!cameraPermissionsAreGranted(requireContext()))
//                    enableCameraPermissions(requestCameraPermissionLauncher)
//                else
//                    openCamera()
//        }

    private val requestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>?> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions?.run {
                if (get(Manifest.permission.READ_EXTERNAL_STORAGE) == true || get(Manifest.permission.READ_MEDIA_IMAGES) == true)
                    openStorage()
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMethodAttachedFileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()
    }

    private fun setListener() {
        binding.btnCamera.setOnClickListener {
            openCamera()
        }
        binding.btnFile.setOnClickListener {
            openStorage()
        }
    }

    private fun navigateUp(methodKey: String){
        setFragmentResult(Constants.REQUEST_METHOD_ATTACHED_FILE_KEY, bundleOf(methodKey to true))
        navController.tryNavigateUp()
    }


    private fun openCamera() {
        if (!cameraPermissionsAreGranted(requireContext())) {
            enableCameraPermissions(requestCameraPermissionLauncher)
            return
        }
        navController.tryNavigate(
            R.id.cameraViewFragment
        )
    }

    private fun openStorage() {
        if (!storagePermissionsAreGranted(requireContext())) {
            enableStoragePermissions(requestMultiplePermissionsLauncher)
            return
        }

        navigateUp(Constants.FILE_KEY)
    }
}