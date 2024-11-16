package com.cancer.yaqeen.presentation.ui.auth.intro.user_type.patient.cancer_type

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.onboarding.models.CancerType
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentSelectCancerTypeBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.auth.OnboardingViewModel
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.ERROR
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvent
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.SELECT_CANCER_TYPE
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.START_CREATE_ACCOUNT
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.UPDATE_CANCER_TYPE
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.UPDATE_USER_TYPE
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SelectCancerTypeFragment : BaseFragment() {

    private var binding: FragmentSelectCancerTypeBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var cancerTypesAdapter: CancerTypesAdapter

    private var layoutPop: View? = null
    private var tvInfo: TextView? = null

    private val viewModel: OnboardingViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSelectCancerTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setupCancerTypesAdapter()

        updateUI()

        setListener()

        observeStates()
    }

    override fun onResume() {
        super.onResume()

        binding.autoTvSearchItems.setText("")
    }

    private fun setListener(){
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnNext.setOnClickListener {
            viewModel.logEvent(
                GoogleAnalyticsEvent(
                    eventName = SELECT_CANCER_TYPE,
                )
            )
            viewModel.updateUserProfile()
        }

        binding.btnPrevious.setOnClickListener {
            navController.tryPopBackStack()
        }

        binding.autoTvSearchItems.addTextChangedListener {
            cancerTypesAdapter.filter.filter(it.toString())
        }
    }
    private fun updateUI() {
        val spannable = SpannableStringBuilder("1/3")
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary_color)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvPageNumber.text = spannable

    }

    private fun observeStates() {
        lifecycleScope {
            viewModel.viewStateLoading.collectLatest {
                onLoading(it)
            }
        }
        lifecycleScope {
            viewModel.viewStateResources.collectLatest {
                it?.let {
                    cancerTypesAdapter.setList(
                        it.cancerTypes
                    )
                    cancerTypesAdapter.currentList.firstOrNull()?.apply {
                        val cancerTypeId = viewModel.getUserProfile()?.cancerTypeId
                        if(cancerTypeId == null)
                            selectCancerType(this)
                        else
                            cancerTypesAdapter.selectItem(cancerTypeId)
                    }
                }

            }
        }
        lifecycleScope {
            viewModel.viewStateUpdateProfileSuccess.collectLatest {
                it?.let {
                    navController.tryNavigate(
                        SelectCancerTypeFragmentDirections.actionSelectCancerTypeFragmentToStagesFragment()
                    )
                }
            }
        }
        lifecycleScope {
            viewModel.viewStateError.collectLatest {
                handleResponseError(it)
            }
        }
    }

    private fun handleResponseError(errorEntity: ErrorEntity?) {
        val errorMessage = handleError(errorEntity)
        displayErrorMessage(errorMessage)
    }

    private fun displayErrorMessage(errorMessage: String?) {
        errorMessage?.let {
            splashViewModel.logEvent(
                GoogleAnalyticsEvent(
                    eventName = UPDATE_CANCER_TYPE,
                    eventParams = arrayOf(
                        ERROR to errorMessage
                    )
                )
            )
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupCancerTypesAdapter() {
        cancerTypesAdapter = CancerTypesAdapter(
            onItemClick = {
                selectCancerType(it)
                navController.tryNavigate(
                    SelectCancerTypeFragmentDirections.actionSelectCancerTypeFragmentToStagesFragment()
                )
//            viewModel.updateUserProfile()
            },
            onInfoClick = { point, info ->
                showPopup(point, info)
            }
        )

        binding.rvCancerTypes.apply {
            adapter = cancerTypesAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(15f, requireContext())
                )
            )
        }

    }

    private fun selectCancerType(it: CancerType) {
        viewModel.selectCancerType(it.id.toInt())
    }


    private fun initPopupView(){
        if (layoutPop != null)
            return

        val viewGroup = requireActivity().findViewById<View>(R.id.popup) as? FrameLayout
        val layoutInflater = requireContext()
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as? LayoutInflater

        layoutPop = layoutInflater?.inflate(R.layout.layout_popup, viewGroup)

        tvInfo = layoutPop?.findViewById<View>(R.id.tv_info_val) as? TextView

    }

    private fun showPopup(point: Point, info: String) {
        initPopupView()

        tvInfo?.text = info

        val popup = PopupWindow(requireContext())
        popup.contentView = layoutPop

        popup.isFocusable = true

        // Some offset to align the popup a bit to the right, and a bit down, relative to button's position.
        val offsetX = 60
        val offsetY = 65

        // Clear the default translucent background
        popup.setBackgroundDrawable(BitmapDrawable())

        popup.showAtLocation(layoutPop, Gravity.NO_GRAVITY, point.x + offsetX, point.y + offsetY)

        tvInfo?.setOnClickListener {
            popup.dismiss()
        }
    }

}