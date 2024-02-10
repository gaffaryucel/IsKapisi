package com.androiddevelopers.freelanceapp.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {
    private val _authState = MutableLiveData<Resource<Boolean>>()
    val authState: LiveData<Resource<Boolean>> get() = _authState
    private val _forgotPassword = MutableLiveData<Resource<Boolean>>()
    val forgotPassword: LiveData<Resource<Boolean>> get() = _forgotPassword
    private val _verificationEmailSent = MutableLiveData<Resource<Boolean>>()
    val verificationEmailSent: LiveData<Resource<Boolean>> get() = _verificationEmailSent

    fun getUser() = firebaseAuth.currentUser
    fun signOut() = firebaseAuth.signOut()

    fun login(email: String, password: String) = viewModelScope.launch {
        _authState.value = Resource.loading(true)
        firebaseRepo.login(email, password)
            .addOnCompleteListener {
                _authState.value = Resource.loading(false)
                if (it.isSuccessful) {
                    _authState.value = Resource.success(true)
                } else {
                    _authState.value =
                        it.exception?.localizedMessage?.let { message ->
                            Resource.error(message, false)
                        }
                }
            }
    }

    fun forgotPassword(email: String) = viewModelScope.launch {
        _forgotPassword.value = Resource.loading(true)
        firebaseRepo.forgotPassword(email)
            .addOnCompleteListener {
                _forgotPassword.value = Resource.loading(false)
                if (it.isSuccessful) {
                    _forgotPassword.value = Resource.success(true)
                } else {
                    _forgotPassword.value =
                        it.exception?.localizedMessage?.let { message ->
                            Resource.error(message, false)
                        }
                }
            }
    }

    fun sendVerificationEmail() = viewModelScope.launch {
        _verificationEmailSent.value = Resource.loading(true)
        getUser()?.let { currentUser ->
            currentUser.sendEmailVerification().addOnCompleteListener {
                _verificationEmailSent.value = Resource.loading(false)
                if (it.isSuccessful) {
                    _verificationEmailSent.value = Resource.success(true)
                } else {
                    _verificationEmailSent.value =
                        it.exception?.localizedMessage?.let { message ->
                            Resource.error(message, false)
                        }
                }
            }
        }
    }
}