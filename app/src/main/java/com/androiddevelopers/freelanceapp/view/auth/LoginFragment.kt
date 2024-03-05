package com.androiddevelopers.freelanceapp.view.auth

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
import com.androiddevelopers.freelanceapp.view.BottomNavigationActivity
import com.androiddevelopers.freelanceapp.viewmodel.auth.LoginViewModel
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
        observeLiveData(viewLifecycleOwner)

        with(binding) {
            buttonLogin.setOnClickListener {
                val email = edittextEmail.text.toString()
                val password = edittextPassword.text.toString()

                //email alanı boş mu?, password alanı 6 karakter ve fazlasımı kontrolünü ypıyoruz
                if (email.isNotEmpty() && password.length > 5) {
                    viewModel.login(email, password)
                } else if (email.isEmpty()) {
                    edittextLayoutEmail.error = it.context.getString(R.string.text_empty_error)
                } else {
                    edittextLayoutPassword.error = it.context.getString(R.string.password_error)
                }
            }

            // kullanıcı şifresini unuttuysa yeni şifre oluşturmak için
            textForgotPassword.setOnClickListener {
                forgotPasswordDialog.setButton(
                    AlertDialog.BUTTON_POSITIVE, context?.getString(R.string.yes)
                ) { _, _ ->
                    val email = edittextEmail.text.toString().trim()

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
        //kullanıcı daha önce giriş yaptıysa direkt ana sayfaya yönlendirmek için eklendi
        verifyEmail()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun verifyEmail() {
        viewModel.getUser()?.let {
            //kullanıcının email adresini onayladığını kontrol ediyoruz
            if (it.isEmailVerified) {
                gotoHome()
            } else {
                //kullanıcı email adresi doğrulanmadıysa uyarı mesajı görüntüler
                verifiedEmailDialog.show()
            }
        }
    }

    private fun gotoHome() {
        val intent = Intent(requireContext(), BottomNavigationActivity::class.java)
        intent.putExtra("login","login")
        requireActivity().finish()
        requireActivity().startActivity(intent)
    }

    private fun observeLiveData(owner: LifecycleOwner) {
        with(viewModel) {
            authState.observe(owner) {
                when (it.status) {
                    Status.LOADING -> it.data?.let { state -> setProgressBar(state) }
                    Status.SUCCESS -> {
                        verifyEmail()
                    }

                    Status.ERROR -> {
                        errorDialog.setMessage("${context?.getString(R.string.login_dialog_error_message)}\n${it.message}")
                        errorDialog.show()
                    }
                }
            }

            forgotPassword.observe(owner) {
                when (it.status) {
                    Status.LOADING -> it.data?.let { state -> setProgressBar(state) }
                    Status.SUCCESS -> {
                        forgotPasswordSuccessDialog.show()
                    }

                    Status.ERROR -> {
                        forgotPasswordDialog.setMessage("${context?.getString(R.string.login_dialog_error_message)}\n${it.message}")
                    }
                }
            }

            verificationEmailSent.observe(owner) {
                when (it.status) {
                    Status.LOADING -> it.data?.let { state -> setProgressBar(state) }
                    Status.SUCCESS -> {
                        verificationEmailSentDialog.show()
                    }

                    Status.ERROR -> {
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
            setMessage(context.getString(R.string.email_verification_message))
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, context.getString(R.string.yes)
            ) { _, _ ->
                viewModel.sendVerificationEmail()
                viewModel.signOut()
            }
            setButton(
                AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.no)
            ) { dialog, _ ->
                dialog.cancel()
                viewModel.signOut()
            }
        }

        with(verificationEmailSentDialog) {
            setTitle(context.getString(R.string.email_verification_title))
            setMessage(context.getString(R.string.email_verification_success_message))
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, context.getString(R.string.ok)
            ) { dialog, _ ->
                dialog.cancel()
            }
        }
        with(verificationEmailSentErrorDialog) {
            setTitle(context.getString(R.string.email_verification_title))
            setMessage(context.getString(R.string.email_verification_error_message))
            setCancelable(false)
            setButton(
                AlertDialog.BUTTON_POSITIVE, context.getString(R.string.yes)
            ) { _, _ ->
                viewModel.sendVerificationEmail()
                viewModel.signOut()
            }
            setButton(
                AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.no)
            ) { dialog, _ ->
                dialog.cancel()
                viewModel.signOut()
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

    private fun setProgressBar(isVisible: Boolean) {
        if (isVisible) {
            binding.loginProgressBar.visibility = View.VISIBLE
        } else {
            binding.loginProgressBar.visibility = View.GONE
        }
    }

}