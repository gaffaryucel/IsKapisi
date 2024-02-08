package com.androiddevelopers.freelanceapp.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EditMainProfileInfoViewModel  @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth,
    private val storage : FirebaseStorage
) : ViewModel() {
    private val storageReference = storage.reference

    private val userId = firebaseAuth.currentUser!!.uid

    private var _message = MutableLiveData<Resource<UserModel>>()
    val message: LiveData<Resource<UserModel>>
        get() = _message

    private var _uploadMessage = MutableLiveData<Resource<UserModel>>()
    val uploadMessage: LiveData<Resource<UserModel>>
        get() = _uploadMessage

    private val _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData

    fun uploadUserProfilePhoto(r: ByteArray) = viewModelScope.launch {
        val photoFileName = "${UUID.randomUUID()}.jpg"
        val photoRef = storageReference.child("users/${userId}/profilePhoto/$photoFileName")

        photoRef.putBytes(r)
            .addOnSuccessListener {
                photoRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        updateUserInfo("profileImageUrl",imageUrl)
                    }
                    .addOnFailureListener { exception ->
                        _uploadMessage.value = Resource.error("cannot acces url", null)
                    }
            }.addOnFailureListener { exception ->
                // Yükleme başarısız olursa, burada hata işleme kodlarınızı yazabilirsiniz.
                _uploadMessage.value =
                    Resource.error("cannot acces url", null)
            }
    }

    fun updateUserInfo(key : String,userPhoto: Any) {
        val photoMap = hashMapOf<String,Any?>(
            key to userPhoto
        )
        firebaseRepo.updateUserData(userId,photoMap)
    }

}