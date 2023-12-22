package com.androiddevelopers.freelanceapp.view

import android.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.androiddevelopers.freelanceapp.databinding.FragmentRegisterBinding
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.RegisterViewModel
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
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirmPassword = binding.confirmPasswordEditText.text.toString()

            viewModel.signUp(email,password,confirmPassword)
        }
        setupDialogs()
        observeLiveData()
    }
    private fun observeLiveData(){
        viewModel.authState.observe(viewLifecycleOwner, Observer {
            when(it.status){
                Status.SUCCESS ->{
                    verificationDialog?.show()
                }
                Status.LOADING->{
                    //progress bar gösterilecek
                }
                Status.ERROR->{
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}