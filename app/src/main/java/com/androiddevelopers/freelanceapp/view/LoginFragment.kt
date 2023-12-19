package com.androiddevelopers.freelanceapp.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.FragmentLoginBinding
import com.androiddevelopers.freelanceapp.viewmodel.LoginViewModel

class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        val view = binding.root
        return view
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            textForgotPassword.setOnClickListener{
                //TODO: firebase şifre hatırlatma metodu eklenecek
            }

            buttonLogin.setOnClickListener {
                gotoLogin(it)
            }

            textRegister.setOnClickListener{
                gotoRegister(it)
            }

        }


    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun gotoLogin(v: View){
        //Navigation.findNavController(v).navigate(??)
    }

    private fun gotoRegister(v: View){
        Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment)
    }
}