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
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment(), OnClickListener {


    private var _binding: FragmentLoginBinding? = null
    private var binding: FragmentLoginBinding? = _binding
    private lateinit var navController: NavController
    private val scope = "openid profile email read:current_user update:current_user_metadata"
    private val account: Auth0 by lazy {
        // -- REPLACE this credentials with your own Auth0 app credentials!
        val account = Auth0(getString(R.string.com_auth0_client_id), getString(R.string.com_auth0_domain))


        // Only enable network traffic logging on production environments!
        account.networkingClient = DefaultClient(enableLogging = true)
        account
    }

    private val audience: String by lazy {
        "https://${getString(R.string.com_auth0_domain)}/api/v2/"
    }
    private val credentialsManager: SecureCredentialsManager by lazy {
        val storage = SharedPreferencesStorage(requireContext())
        val manager = SecureCredentialsManager(requireContext(), authenticationApiClient, storage)
        manager
    }
    private val authenticationApiClient: AuthenticationAPIClient by lazy {
        AuthenticationAPIClient(account)
    }

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
            _binding!!.btnLogin.setOnClickListener {
                login()
            }
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
        Log.e("Clicked","loginbtn clicked")
        var email = _binding?.emailAuthET?.text.toString()
        var password = _binding?.passwordAuthEt?.text.toString()
        dbLogin(email,password)
    }

    private fun dbLogin(email: String, password: String) {
        WebAuthProvider.login(account)
            .withScheme("demo")
            .withScope("openid profile email read:current_user update:current_user_metadata")
            .withAudience("https://${getString(R.string.com_auth0_domain)}/api/v2/")

            // Launch the authentication passing the callback where the results will be received
            .start(requireContext(), object : Callback<Credentials, AuthenticationException> {
                override fun onFailure(exception: AuthenticationException) {
                }

                override fun onSuccess(credentials: Credentials) {

                }
            })
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