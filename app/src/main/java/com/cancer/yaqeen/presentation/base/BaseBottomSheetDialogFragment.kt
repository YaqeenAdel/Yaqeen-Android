package com.cancer.yaqeen.presentation.base

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.data.utils.handleError
import com.cancer.yaqeen.presentation.ui.MainActivity
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("MissingPermission")
@AndroidEntryPoint
open class BaseBottomSheetDialogFragment constructor(private val showBottomMenu: Boolean) : BottomSheetDialogFragment() {

    private lateinit var dialogForceLogout: AlertDialog

    @Inject
    lateinit var prefUtil: SharedPrefEncryptionUtil

    constructor() : this(showBottomMenu = false)

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).displayBottomMenu(showBottomMenu)

        onLoading(false)
    }

    fun FragmentActivity.hideSoftKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        currentFocus?.clearFocus()
    }

    fun showNavBarAndHeader(show: Boolean) {
        try {
//            (requireActivity() as? MainActivity)?.displayBottomMenu(show)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun lifecycleScope(
        block: suspend CoroutineScope.() -> Unit
    ){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                block()
            }
        }
    }

    fun onLoading(show: Boolean) {
        try {
            (requireActivity() as? MainActivity)?.displayLoading(show)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun handleError(errorEntity: ErrorEntity?): String? {
        return requireContext().handleError(errorEntity){
            onAccessDenied()
        }
    }

    private fun onAccessDenied() {
        if (!::dialogForceLogout.isInitialized) {
            val builder = AlertDialog.Builder(requireContext())
            builder.apply {
                setTitle(getString(R.string.token_expired_title))
                setMessage(getString(R.string.token_expired_message))
                setPositiveButton(getString(R.string.login_dialog)) { dialog, _ ->
                    dialog.dismiss()
                    forceLogout()
//                    onLoginAgain()
                }
                setCancelable(false)
            }
            dialogForceLogout = builder.create()
            dialogForceLogout.apply {
                window?.setBackgroundDrawableResource(R.color.white)
                setOnShowListener {
                    getButton(DialogInterface.BUTTON_POSITIVE).let { positiveButton ->
                        positiveButton.setTextColor(Color.BLUE)
                        positiveButton.setBackgroundColor(Color.WHITE)
                    }
                }
                show()
            }
        }
    }

    private fun forceLogout() {
        prefUtil.clearUserPreferenceStorage()

        val findNavController =
            requireActivity().findNavController(R.id.activity_main_nav_host_fragment)
        findNavController
            .tryNavigate(
                R.id.splashFragment,
                null,
                NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build()
            )

    }
}