package com.androiddevelopers.freelanceapp.viewmodel

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
    val authState = MutableLiveData<Resource<Boolean>>()
    val verifiedEmail = MutableLiveData<Resource<Boolean>>()

    fun getUser() = firebaseAuth.currentUser
    fun signOut() = firebaseAuth.signOut()

    //fun login(email: String, password: String) = firebaseRepo.login(email, password)
    fun login(email: String, password: String) = viewModelScope.launch {
        firebaseRepo.login(email, password).addOnSuccessListener {
            verifiedEmail.value = Resource.success(it.user?.isEmailVerified)
        }.addOnFailureListener {
            verifiedEmail.value =
                it.localizedMessage?.let { message ->
                    Resource.error(message, false)
                }
        }
    }

    fun forgotPassword(email: String) = firebaseRepo.forgotPassword(email)

    fun getUserDataByDocumentId(documentId: String) =
        firebaseRepo.getUserDataByDocumentId(documentId)
}