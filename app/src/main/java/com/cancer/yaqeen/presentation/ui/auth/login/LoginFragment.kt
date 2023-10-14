package com.cancer.yaqeen.presentation.ui.auth.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.SecureCredentialsManager
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.callback.Callback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.request.DefaultClient
import com.auth0.android.result.Credentials
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentLoginBinding
import com.cancer.yaqeen.presentation.ui.auth.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(), OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private var binding: FragmentLoginBinding? = _binding
    private lateinit var navController: NavController

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
         return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        setListener()

    }

    private fun setListener() {
        _binding?.let {
            _binding!!.btnLogin.setOnClickListener(this)
            _binding!!.btnSignup.setOnClickListener(this)
            _binding!!.btnForgotPassword.setOnClickListener(this)
        }
    }

    private fun navigateToForgotPasswordScreen() {
        navController.navigate(
            LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
        )
    }

    private fun navigateToSignupScreen() {
        navController.navigate(
            LoginFragmentDirections.actionLoginFragmentToSignUpFragment()
        )
    }

    private fun login() {
//        authViewModel.login()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btn_login -> {
                login()
            }
            R.id.btn_signup -> {
                navigateToSignupScreen()
            }
            R.id.btn_forgot_password -> {
                navigateToForgotPasswordScreen()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}