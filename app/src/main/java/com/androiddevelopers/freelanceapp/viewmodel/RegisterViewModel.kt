package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseRepo : FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {

    private var _authState = MutableLiveData<Resource<Boolean>>()
    val authState : LiveData<Resource<Boolean>>
        get() = _authState

    private var _registrationError = MutableLiveData<Resource<Boolean>>()
    val registrationError : LiveData<Resource<Boolean>>
        get() = _registrationError

    private var _isVerificationEmailSent = MutableLiveData<Resource<Boolean>>()
    val isVerificationEmailSent : LiveData<Resource<Boolean>>
        get() = _isVerificationEmailSent

    fun signUp(
        email: String,
        password: String,
        confirmPassword : String
    ) = viewModelScope.launch{
        _authState.value = Resource.loading(null)
        if (isInputValid(email,password,confirmPassword)){
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener{task->
                    if (task.isSuccessful){
                        val userId = firebaseAuth.currentUser?.uid ?: ""
                        createUser(userId,email)
                        _authState.value = Resource.success(null)
                    }else{
                        _authState.value = Resource.error(task.exception?.localizedMessage ?: "error : try again",null)
                    }
                }
        }else{
            _authState.value = Resource.error("password mismatch",null)
        }
    }

    private fun createUser(
        userId : String,
        email: String
    ) = viewModelScope.launch{
        val tempUsername = email.substringBefore("@")
        val user = makeUser(userId,tempUsername,email)
        firebaseRepo.addUserToFirestore(user)
            .addOnSuccessListener {
                verify()
            }.addOnFailureListener { e ->
                _authState.value = Resource.error(e.localizedMessage ?: "error : try again later",null)
            }
    }

    private fun verify() = viewModelScope.launch{
        val current = firebaseAuth.currentUser
        current?.sendEmailVerification()?.addOnCompleteListener {
            if (it.isSuccessful) {
                _isVerificationEmailSent.value = Resource.success(it.isSuccessful)
                firebaseAuth.signOut()
            } else {
                _isVerificationEmailSent.value = Resource.error(it.exception?.localizedMessage ?: "error",null)
            }
        }?.addOnFailureListener{
            _isVerificationEmailSent.value = Resource.error( it.localizedMessage ?: "error",null)
        }
    }

    private fun makeUser(userId : String,userName: String,email: String) : UserModel {
        return UserModel(userId,userName,email,"")
    }

    private fun isPasswordConfirmed(password: String,confirmPassword : String): Boolean {
        return if (password == confirmPassword){
            true
        }else{
            _registrationError.value = Resource.error("şifreler uyuşmuyor", false)
            false
        }
    }

    private fun isInputValid(email : String, password: String,confirmPassword : String) : Boolean {
        return if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            val errorMessage = "Lütfen tüm alanları doldurun."
            _registrationError.value = Resource.error(errorMessage, true)
            false
        } else {
            isPasswordConfirmed(password,confirmPassword)
        }
    }
}