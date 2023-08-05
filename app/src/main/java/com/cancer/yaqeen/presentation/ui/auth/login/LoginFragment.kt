package com.cancer.yaqeen.presentation.ui.auth.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentLoginBinding

class LoginFragment : Fragment(), OnClickListener {

    private var _binding: FragmentLoginBinding? = null
    private var binding: FragmentLoginBinding? = _binding

    private lateinit var navController: NavController

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
        binding?.let {
            binding!!.btnLogin.setOnClickListener(this)
            binding!!.btnSignup.setOnClickListener(this)
            binding!!.btnForgotPassword.setOnClickListener(this)
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