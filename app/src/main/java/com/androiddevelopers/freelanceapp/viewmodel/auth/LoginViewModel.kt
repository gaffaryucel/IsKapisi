package com.androiddevelopers.freelanceapp.viewmodel.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    private var userToken = MutableLiveData<Resource<String>>()

    fun getUser() = firebaseAuth.currentUser
    fun signOut() = firebaseAuth.signOut()

    init {
        getToken()
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _authState.value = Resource.loading(true)
        firebaseRepo.login(email, password)
            .addOnCompleteListener {
                _authState.value = Resource.loading(false)
                if (it.isSuccessful) {
                    _authState.value = Resource.success(true)
                    updateUserToken(it.result.user?.uid.toString())
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

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                userToken.value = Resource.error("", null)
                return@addOnCompleteListener
            }
            val token = it.result //this is the token retrieved
            userToken.value = Resource.success(token)
        }
    }

    private fun updateUserToken(currentUserId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val tokenMap = hashMapOf<String, Any?>(
                "token" to userToken.value?.data
            )
            firebaseRepo.updateUserData(currentUserId, tokenMap)
        }
    }
}