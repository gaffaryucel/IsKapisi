package com.androiddevelopers.freelanceapp.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.androiddevelopers.freelanceapp.util.toUserModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditProfileServiceInfoViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    firebaseAuth: FirebaseAuth,
) : ViewModel() {

    private val userId = firebaseAuth.currentUser!!.uid

    private var _message = MutableLiveData<Resource<UserModel>>()
    val message: LiveData<Resource<UserModel>>
        get() = _message

    private val _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData

    fun updateUserInfo(key: String, userhoto: Any) {
        val photoMap = hashMapOf<String, Any?>(
            key to userhoto
        )
        firebaseRepo.updateUserData(userId, photoMap)
    }

    init {
        getUserDataFromFirebase()
    }

    private fun getUserDataFromFirebase() {
        firebaseRepo.getUserDataByDocumentId(userId).addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                documentSnapshot.toUserModel()?.let { userModel ->
                    _userData.value = userModel
                    _message.value = Resource.success(null)
                } ?: run {
                    _message.value = Resource.error("Belirtilen belge bulunamadı", null)
                }
            } else {
                // Belge yoksa işlemleri buraya ekleyebilirsiniz
                _message.value = Resource.error("kullanıcı kaydedilmemiş", null)
            }
        }.addOnFailureListener { exception ->
            // Hata durzumunda işlemleri buraya ekleyebilirsiniz
            println("Belge alınamadı. Hata: $exception")
            _message.value = Resource.error("Belge alınamadı. Hata: $exception", null)
        }
    }
}