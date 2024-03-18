package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BottomNavigationViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    auth: FirebaseAuth
) : ViewModel() {

    private val currentUserId = auth.currentUser?.uid



    fun setUserOnline() {
        if (currentUserId != null){
            firebaseRepo.changeOnlineStatus(currentUserId,true).addOnSuccessListener {
                println("setUserOnline")
            }
        }
    }
    fun setUserOffline(){
        if (currentUserId != null){
            firebaseRepo.changeOnlineStatus(currentUserId,false).addOnSuccessListener {
                println("setUserOffline")
            }
        }
    }
}