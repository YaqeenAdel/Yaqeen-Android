package com.cancer.yaqeen.presentation.ui.main.treatment.add.symptoms.details

import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomTrack
import com.cancer.yaqeen.databinding.FragmentSymptomsDetailsBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.add.symptoms.SymptomsViewModel
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.Constants.IMAGE_URI
import com.cancer.yaqeen.presentation.util.Constants.IMAGE_URI_KEY
import com.cancer.yaqeen.presentation.util.Constants.IMAGE_URL
import com.cancer.yaqeen.presentation.util.Constants.REQUEST_TAKE_PICTURE_KEY
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.disable
import com.cancer.yaqeen.presentation.util.enable
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SymptomsDetailsFragment : BaseFragment() {

    private var binding: FragmentSymptomsDetailsBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var attachedPhotosAdapter: AttachedPhotosAdapter

    private val symptomsViewModel: SymptomsViewModel by activityViewModels()


    private val getContentResultLauncher: ActivityResultLauncher<String?> =
        registerForActivityResult(
            ActivityResultContracts.GetMultipleContents()
        ) { result ->

            if(result.isNullOrEmpty())
                return@registerForActivityResult

            if (result.size == 1)
                addImage(result.first())
            else
                addImages(result)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSymptomsDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(Constants.REQUEST_METHOD_ATTACHED_FILE_KEY) { requestKey, bundle ->
            if(requestKey == Constants.REQUEST_METHOD_ATTACHED_FILE_KEY) {
                val fileMethod = bundle.getBoolean(Constants.FILE_KEY)

                if(fileMethod)
                    openStorage()

            }
        }

        setFragmentResultListener(REQUEST_TAKE_PICTURE_KEY) { requestKey, bundle ->
            if(requestKey == REQUEST_TAKE_PICTURE_KEY) {
                val imageUri = bundle.getString(IMAGE_URI_KEY)
                val uri = imageUri?.toUri()

                uri?.let {
                    addImage(uri)
                }
            }
        }

        navController = findNavController()

        setupAttachedPhotosAdapter()

        updateUI()

        setListener()
    }

    override fun onResume() {
        super.onResume()

        val symptomTrack = symptomsViewModel.getSymptomTrack()
        updateUI(symptomTrack)
    }

    private fun updateUI(symptomTrack: SymptomTrack?) {
        symptomTrack?.run {
            if (details?.isNotEmpty() == true)
                binding.editTextDetails.setText(details)

            if(photosList?.isNotEmpty() == true)
                attachedPhotosAdapter.setList(photosList!!.toList())
        }

        checkSymptomData()
    }

    private fun addImage(uri: Uri){
        val photo = symptomsViewModel.createPhoto(uri)
        attachedPhotosAdapter.addPicture(photo)
        symptomsViewModel.addSymptomPhoto(photo)
    }

    private fun addImages(uris: List<Uri>){
        val photos = symptomsViewModel.createPhotos(uris)
        attachedPhotosAdapter.addPictures(photos)
        symptomsViewModel.addSymptomPhotos(photos)
    }

    private fun setupAttachedPhotosAdapter() {
        if (!::attachedPhotosAdapter.isInitialized) {
            attachedPhotosAdapter = AttachedPhotosAdapter(
                onShowClick = {
                    navController.tryNavigate(
                        R.id.photoFullScreenFragment, bundleOf(
                            IMAGE_URI to it.uri?.toString(),
                            IMAGE_URL to it.url,
                        )
                    )
                },
                onDeleteClick = {
                    symptomsViewModel.deleteSymptomPhoto(it)
                }
            )
        }

        binding.rvImages.apply {
            adapter = attachedPhotosAdapter
        }

    }

    private fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnAttachImage.setOnClickListener {
            navController.tryNavigate(
                R.id.methodAttachedFileFragment
            )
        }

        binding.btnNext.setOnClickListener {
            val details = binding.editTextDetails.text.toString().trim()
            symptomsViewModel.setSymptomData(details)
            navController.tryNavigate(
                SymptomsDetailsFragmentDirections.actionSymptomsDetailsFragmentToChooseTimeSymptomFragment()
            )
        }

        binding.editTextDetails.addTextChangedListener {
            checkSymptomData()
        }
    }

    private fun checkSymptomData() {
        val details = binding.editTextDetails.text.toString().trim()

        val textColorId: Int
        val backgroundColorId: Int

        if (details.isNotEmpty()) {
            binding.btnNext.enable()
            textColorId = R.color.white
            backgroundColorId = R.color.primary_color
        } else {
            binding.btnNext.disable()
            textColorId = R.color.medium_gray
            backgroundColorId = R.color.light_gray
        }

        binding.btnNext.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), backgroundColorId)
        binding.btnNext.setTextColor(ContextCompat.getColorStateList(requireContext(), textColorId))
        binding.btnNext.iconTint = ContextCompat.getColorStateList(requireContext(), textColorId)
    }

    private fun updateUI() {
        val spannable = SpannableStringBuilder("2/3")
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary_color)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvPageNumber.text = spannable

    }

    private fun openStorage() {
        getContentResultLauncher.launch("image/*")
    }
}