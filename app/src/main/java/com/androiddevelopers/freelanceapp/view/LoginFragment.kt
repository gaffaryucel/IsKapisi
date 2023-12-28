package com.androiddevelopers.freelanceapp.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.FragmentLoginBinding
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var errorDialog: AlertDialog
    private lateinit var verifiedEmailDialog: AlertDialog
    private lateinit var forgotPasswordDialog: AlertDialog
    private lateinit var forgotPasswordSuccessDialog: AlertDialog
    private lateinit var verificationEmailSentDialog: AlertDialog
    private lateinit var verificationEmailSentErrorDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        errorDialog = AlertDialog.Builder(context).create()
        verifiedEmailDialog = AlertDialog.Builder(context).create()
        forgotPasswordDialog = AlertDialog.Builder(context).create()
        forgotPasswordSuccessDialog = AlertDialog.Builder(context).create()
        verificationEmailSentDialog = AlertDialog.Builder(context).create()
        verificationEmailSentErrorDialog = AlertDialog.Builder(context).create()

        setProgressBar(false)
        setupDialogs()
        observeLiveData(binding.root, viewLifecycleOwner)

        with(binding) {
            buttonLogin.setOnClickListener {
                val email = edittextEmail.text.toString()
                val password = edittextPassword.text.toString()

                if (email.isNotEmpty() && password.length > 5) {
                    viewModel.login(email, password)
                } else if (email.isEmpty()) {
                    edittextLayoutEmail.error = it.context.getString(R.string.text_empty_error)
                } else {
                    edittextLayoutPassword.error = it.context.getString(R.string.password_error)
                }
            }

            textForgotPassword.setOnClickListener {
                forgotPasswordDialog.setButton(
                    AlertDialog.BUTTON_POSITIVE, context?.getString(R.string.yes)
                ) { _, _ ->
                    val email = edittextEmail.text.toString()

                    if (email.isNotEmpty()) {
                        viewModel.forgotPassword(email)
                    } else {
                        edittextLayoutEmail.error = it.context.getString(R.string.text_empty_error)
                    }
                }
                forgotPasswordDialog.show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        verifyEmail() //kullanıcı daha önce giriş yaptıysa direkt ana sayfaya yönlendirmek için eklendi
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun verifyEmail() {
        viewModel.getUser()?.let {
            if (it.isEmailVerified) { //kullanıcının email adresini onayladığını kontrol ediyoruz
                view?.let { v -> gotoHome(v) }
            } else {
                //kullanıcı email adresi doğrulanmadıysa uyarı mesajı görüntüler
                verifiedEmailDialog.show()
            }
        }
    }

    private fun gotoHome(v: View) {
//        val intent = Intent(requireContext(), BottomNavigationActivity::class.java)
//        requireActivity().finish()
//        requireActivity().startActivity(intent)
    }

    private fun observeLiveData(view: View, owner: LifecycleOwner) {
        with(viewModel) {
            authState.observe(owner) {
                when (it.status) {
                    Status.LOADING -> setProgressBar(true)
                    Status.SUCCESS -> {
                        setProgressBar(false)
                        verifyEmail()
                    }
                    Status.ERROR -> {
                        setProgressBar(false)
                        errorDialog.setMessage("${context?.getString(R.string.login_dialog_error_message)}\n${it.message}")
                        errorDialog.show()
                    }
                }
            }

            forgotPassword.observe(owner) {
                when (it.status) {
                    Status.LOADING -> setProgressBar(true)
                    Status.SUCCESS -> {
                        setProgressBar(false)
                        forgotPasswordSuccessDialog.show()
                    }
                    Status.ERROR -> {
                        setProgressBar(false)
                        forgotPasswordDialog.setMessage("${context?.getString(R.string.login_dialog_error_message)}\n${it.message}")
                    }
                }
            }

            verificationEmailSent.observe(owner) {
                when (it.status) {
                    Status.LOADING -> setProgressBar(true)
                    Status.SUCCESS -> {
                        setProgressBar(false)
                        verificationEmailSentDialog.show()
                    }
                    Status.ERROR -> {
                        setProgressBar(false)
                        verificationEmailSentErrorDialog.show()
                    }
                }
            }

        }
    }

    private fun setupDialogs() {
        with(errorDialog) {
            setTitle(context.getString(R.string.login_dialog_error))
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok)
            ) { dialog, _ ->
                dialog.cancel()
            }
        }

        with(verifiedEmailDialog) {
            setTitle(context.getString(R.string.email_verification_title))
            setMessage("${viewModel.getUser()?.email} \n ${context.getString(R.string.email_verification_message)}")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, context.getString(R.string.yes)
            ) { _, _ ->
                viewModel.sendVerificationEmail()
            }
            setButton(
                AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.no)
            ) { dialog, _ ->
                dialog.cancel()
            }
        }

        with(verificationEmailSentDialog) {
            setTitle(context.getString(R.string.email_verification_title))
            setMessage("${viewModel.getUser()?.email} \n ${context.getString(R.string.email_verification_success_message)}")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok)
            ) { dialog, _ ->
                dialog.cancel()
            }
        }
        with(verificationEmailSentErrorDialog) {
            setTitle(context.getString(R.string.email_verification_title))
            setMessage("${viewModel.getUser()?.email} \n ${context.getString(R.string.email_verification_error_message)}")
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, context.getString(R.string.yes)
            ) { _, _ ->
                viewModel.sendVerificationEmail()
            }
            setButton(
                AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.no)
            ) { dialog, _ ->
                dialog.cancel()
            }
        }

        with(forgotPasswordDialog) {
            setTitle(context.getString(R.string.email_forgot_password_title))
            setMessage(context.getString(R.string.email_forgot_password_message))
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.no)
            ) { dialog, _ ->
                dialog.cancel()
            }
        }

        with(forgotPasswordSuccessDialog) {
            setTitle(context.getString(R.string.email_forgot_password_title))
            setMessage(context.getString(R.string.email_forgot_password_message_success))
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok)
            ) { dialog, _ ->
                dialog.cancel()
            }
        }
    }

    private fun setProgressBar(visible: Boolean){
        if (visible){
            binding.loginProgressBar.visibility = View.VISIBLE
        }else{
            binding.loginProgressBar.visibility = View.GONE
        }
    }

}