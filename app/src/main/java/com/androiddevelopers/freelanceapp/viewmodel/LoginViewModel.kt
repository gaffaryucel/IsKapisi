package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {
    fun getUser() = firebaseAuth.currentUser
    fun signOut() = firebaseAuth.signOut()

    fun login(email: String, password: String) = firebaseRepo.login(email, password)

    fun forgotPassword(email: String) = firebaseRepo.forgotPassword(email)

    fun getUserDataByDocumentId(documentId: String) = firebaseRepo.getUserDataByDocumentId(documentId)
}