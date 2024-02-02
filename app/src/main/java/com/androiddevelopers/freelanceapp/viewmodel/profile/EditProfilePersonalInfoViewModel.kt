package com.androiddevelopers.freelanceapp.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditProfilePersonalInfoViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {
    private var _message = MutableLiveData<Resource<UserModel>>()
    val message: LiveData<Resource<UserModel>>
        get() = _message

    private val userId = firebaseAuth.currentUser!!.uid

    fun updateUserInfo(key : String,userInfo: Any) {
        _message.value = Resource.loading(null)
        val photoMap = hashMapOf<String,Any?>(
            key to userInfo
        )
        firebaseRepo.updateUserData(userId,photoMap).addOnSuccessListener {
            _message.value = Resource.success(null)
        }.addOnFailureListener{
            _message.value = it.localizedMessage?.let { it1 -> Resource.error(it1,null) }
        }
    }
}