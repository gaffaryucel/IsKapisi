package com.androiddevelopers.freelanceapp.view.auth

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.freelanceapp.databinding.FragmentRegisterBinding
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.auth.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private lateinit var viewModel: RegisterViewModel

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private var verificationDialog: AlertDialog? = null
    private var errorDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorDialog = AlertDialog.Builder(requireContext()).create()
        verificationDialog = AlertDialog.Builder(requireContext()).create()


        binding.buttonRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            viewModel.signUp(email,password,confirmPassword)
        }

        binding.tvGoToLogin.setOnClickListener {
            goBackLogin()
        }
        setupDialogs()
        observeLiveData()
    }
    private fun observeLiveData(){
        viewModel.authState.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.ERROR->{
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    binding.pbRegister.visibility = View.INVISIBLE
                    binding.buttonRegister.isEnabled = true
                }
                Status.LOADING->{
                    binding.pbRegister.visibility = View.VISIBLE
                    binding.buttonRegister.isEnabled = false
                }
                Status.SUCCESS->{
                    binding.pbRegister.visibility = View.INVISIBLE
                    binding.buttonRegister.isEnabled = true
                }
            }
        })

        viewModel.registrationError.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.ERROR -> {
                    if (it.data == true){
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }else{
                        binding.confirmPasswordEditText.error = it.message
                    }
                }
                else -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.isVerificationEmailSent.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    verificationDialog?.show()
                }
                Status.ERROR -> {
                    errorDialog?.show()
                }
                else->{
                    errorDialog?.show()
                }
            }
        })
    }
    private fun setupDialogs() {
        verificationDialog?.setTitle("Email Doğrulama")
        verificationDialog?.setMessage("Doğrulama e-postası gönderildi. Lütfen e-posta kutunuzu kontrol edin.")
        verificationDialog?.setCancelable(false)

        verificationDialog?.setButton(AlertDialog.BUTTON_POSITIVE, "Tamam") { _, _ ->
            findNavController().popBackStack()
        }

        errorDialog?.setTitle("Hata")
        errorDialog?.setMessage("e-posta gönderilemedi")
        errorDialog?.setCancelable(false)

        errorDialog?.setButton(AlertDialog.BUTTON_POSITIVE, "Tekrar gönder") { _, _ ->

        }
    }

    private fun goBackLogin(){
        findNavController().popBackStack()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}