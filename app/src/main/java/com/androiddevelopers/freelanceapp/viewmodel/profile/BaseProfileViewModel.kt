package com.androiddevelopers.freelanceapp.viewmodel.profile

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.androiddevelopers.freelanceapp.util.toUserModel
import com.androiddevelopers.freelanceapp.viewmodel.BaseNotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@HiltViewModel
open class BaseProfileViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth,
) : BaseNotificationViewModel(firebaseRepo, firebaseAuth) {

    private var _message = MutableLiveData<Resource<UserModel>>()
    val message: LiveData<Resource<UserModel>>
        get() = _message

    private var _uploadMessage = MutableLiveData<Resource<UserModel>>()
    val uploadMessage: LiveData<Resource<UserModel>>
        get() = _uploadMessage

    private val _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData


    init {
        getUserDataFromFirebase()
    }

    internal fun getUserDataFromFirebase() {
        _message.value = Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepo.getUserDataByDocumentId(currentUserId)
                .addOnSuccessListener { documentSnapshot ->
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
                }
                .addOnFailureListener { exception ->
                    // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                    _message.value = Resource.error("Belge alınamadı. Hata: $exception", null)
                }
        }
    }

    internal suspend fun saveImageToStorage(bitmap: Bitmap, path: String): String? {
        return suspendCoroutine { continuation ->
            viewModelScope.launch {
                _uploadMessage.value = Resource.loading(null)
                val imageUrl = firebaseRepo.uploadPhotoToStorage(bitmap, currentUserId, path)
                if (path.equals("profile")) {
                    if (imageUrl != null) {
                        try {
                            updateUserInfo("profileImageUrl", imageUrl)
                            _uploadMessage.value = Resource.success(null)
                        } catch (e: Exception) {
                            _uploadMessage.value =
                                e.localizedMessage?.let { Resource.error(it, null) }
                            continuation.resumeWithException(e) // Hata durumunda devam et
                        }
                    } else {
                        _uploadMessage.value = Resource.error("Hata", null)
                        continuation.resume(null) // null döndür
                    }
                } else {
                    continuation.resume(imageUrl)
                }
            }
        }
    }


    internal fun updateUserInfo(key: String, userData: Any) {
        viewModelScope.launch(Dispatchers.IO) {
            val photoMap = hashMapOf<String, Any?>(
                key to userData
            )
            firebaseRepo.updateUserData(currentUserId, photoMap).addOnSuccessListener {
                _message.value = Resource.success(null)
            }.addOnFailureListener {
                _message.value = Resource.error(it.localizedMessage ?: "error", null)
            }
        }
    }

    fun signOut() = firebaseAuth.signOut()

}
