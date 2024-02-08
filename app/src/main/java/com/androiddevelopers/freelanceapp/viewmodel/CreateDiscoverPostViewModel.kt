package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateDiscoverPostViewModel @Inject constructor(
    private val repo : FirebaseRepoInterFace,
    private val storage : FirebaseStorage,
    private val auth : FirebaseAuth
) : ViewModel() {

    private val storageReference = storage.reference
    private val currentUserId = auth.currentUser?.let { it.uid }

    private val _uploadPhotoMessage = MutableLiveData<Resource<String>>()
    val uploadPhotoMessage : LiveData<Resource<String>> = _uploadPhotoMessage

    private val _userData = MutableLiveData<UserModel>()
    val userData : LiveData<UserModel> = _userData

    init {
        getUserDataFromFirebase()
    }
    fun uploadPostPicture(postModel : DiscoverPostModel, r: ByteArray) = viewModelScope.launch {
        _uploadPhotoMessage.value = Resource.loading("loading")

        val photoFileName = "${UUID.randomUUID()}.jpg"
        val photoRef = storageReference.child("users/${currentUserId}/postPhotos/$photoFileName")

        photoRef.putBytes(r)
            .addOnSuccessListener {
                photoRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        val imageUrl = uri.toString()
                        postModel.images = listOf(imageUrl)
                        uploadPostToFirestore(postModel)
                    }
                    .addOnFailureListener { exception ->
                        // URL alınamazsa burada hata işleme kodlarınızı yazabilirsiniz.
                        _uploadPhotoMessage.value =
                            Resource.error("cannot acces url", exception.localizedMessage)
                    }
            }.addOnFailureListener { exception ->
                // Yükleme başarısız olursa, burada hata işleme kodlarınızı yazabilirsiniz.
                _uploadPhotoMessage.value =
                    Resource.error("cannot upload photo", exception.localizedMessage)
            }
    }

    private fun uploadPostToFirestore(postModel: DiscoverPostModel){
        repo.uploadDiscoverPostToFirestore(postModel)
        updateUserData(postModel)
    }

    fun createDiscoverPostModel(
        description : String,
        tags : List<String>
    ) : DiscoverPostModel{
        val postId = UUID.randomUUID().toString()
        return DiscoverPostModel(
            postId,currentUserId,
            description,tags,
            emptyList(),getCurrentTime()
        )
    }
    private fun getCurrentTime(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date(currentTime)
        return dateFormat.format(date)
    }
    private fun updateUserData(discoverPost : DiscoverPostModel){
        repo.uploadDataInUserNode(currentUserId.toString(),discoverPost,"discover",discoverPost.postId.toString())
    }

    private fun getUserDataFromFirebase() {
        repo.getUserDataByDocumentId(currentUserId.toString())
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(UserModel::class.java)
                    if (user != null) {
                        _userData.value = user ?: UserModel()
                    }
                }
            }
    }
}