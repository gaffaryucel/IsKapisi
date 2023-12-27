package com.androiddevelopers.freelanceapp.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.androiddevelopers.freelanceapp.R
import com.androiddevelopers.freelanceapp.databinding.FragmentLoginBinding
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.util.Status
import com.androiddevelopers.freelanceapp.viewmodel.LoginViewModel
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var viewModel: LoginViewModel

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onStart() {
        super.onStart()

        //kullanıcı daha önce giriş yaptıysa direkt ana sayfaya yönlendirmek için eklendi
        viewModel.getUser().let {
            if (it != null) {
                if (it.isEmailVerified) { //kullanıcının email adresini onayladığını kontrol ediyoruz
                    verifiedEmail(it)
                } else {
                    //kullanıcı email adresi doğrulanmadıysa uyarı mesajı görüntüler
                    notVerifiedEmail(it)
                }
            }
        }
    }


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

        with(binding) {
            buttonLogin.setOnClickListener {
                val email = edittextEmail.text.toString()
                val password = edittextPassword.text.toString()

                if (email.isNotEmpty() && password.length > 6) {
                    viewModel.login(email, password)
                } else if (email.isEmpty()) {
                    edittextLayoutEmail.error = it.context.getString(R.string.text_empty_error)
                } else {
                    edittextLayoutPassword.error = it.context.getString(R.string.password_error)
                }
            }

            textRegister.setOnClickListener {
                gotoRegister(it)
            }

            textForgotPassword.setOnClickListener {
                val email = edittextEmail.text.toString()

                if (email.isNotEmpty()) {
                    viewModel.forgotPassword(email)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Parola sıfırlama maili gönderildi.",
                                Toast.LENGTH_LONG
                            ).show()
                        }.addOnFailureListener { e ->
                            e.localizedMessage?.let { message ->
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        }
                } else {
                    edittextLayoutEmail.error = it.context.getString(R.string.text_empty_error)
                }

            }
        }
        observeLiveData(binding.root)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun gotoRegister(v: View) {
        Navigation.findNavController(v).navigate(R.id.action_loginFragment_to_registerFragment)
    }

    private fun gotoHome(v: View) {
        val intent = Intent(requireContext(), BottomNavigationActivity::class.java)
        requireActivity().finish()
        requireActivity().startActivity(intent)
    }

    private fun setUserModelToRoom(userModel: UserModel) {
        //TODO: Room 'a user kayıt edilecek
    }

    private fun verifiedEmail(currentUser: FirebaseUser) {
        viewModel.getUserDataByDocumentId(currentUser.uid)
            .addOnSuccessListener { snapshot ->
                snapshot.toObject(
                    UserModel::class.java
                )?.let { userModel ->
                    setUserModelToRoom(userModel) //firebase 'ten gelen kullanıcı bilgilerini room'a yazmak için
                    view?.let { v ->
                        gotoHome(v)
                    }
                }
            }.addOnFailureListener { e ->
                e.localizedMessage?.let { message ->
                    Toast.makeText(
                        context,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun observeLiveData(view: View) {
        viewModel.verifiedEmail.observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> gotoHome(view)
                //Status.ERROR -> notVerifiedEmail()
            }
        }
    }

    private fun notVerifiedEmail(currentUser: FirebaseUser) {
        currentUser.email?.let { email ->
            val builder = AlertDialog.Builder(context)
            builder.setTitle("E-Posta Doğrulama")
            builder.setMessage("$email adresiniz doğrulanmamış, tekrar doğrulama e-postası gönderilmesini ister misiniz?")
            builder.setPositiveButton("Evet") { dialog, _ ->
                dialog.cancel()
                currentUser.verifyBeforeUpdateEmail(email)
                    .addOnCompleteListener {
                        Toast.makeText(
                            context,
                            "Eposta doğrulama mesajı gönderildi.",
                            Toast.LENGTH_LONG
                        ).show()
                    }.addOnFailureListener { e ->
                        e.localizedMessage?.let { message ->
                            Toast.makeText(
                                context,
                                message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
            builder.setNegativeButton("Hayır") { dialog, _ ->
                dialog.cancel()
                viewModel.signOut()
            }
            builder.create().show()
        }
    }

}