package com.androiddevelopers.freelanceapp.viewmodel.profile

import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditUserProfileInfoViewModel  @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth,
): BaseProfileViewModel(firebaseRepo,firebaseAuth) {

    fun signOut(){
        firebaseAuth.signOut()
    }

}