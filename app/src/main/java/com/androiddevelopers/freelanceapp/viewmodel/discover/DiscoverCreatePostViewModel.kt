package com.androiddevelopers.freelanceapp.viewmodel.discover

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DiscoverCreatePostViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val userId = firebaseAuth.currentUser?.uid.toString()

    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private val _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel> = _userData

    private var _liveDateBitmapImages = MutableLiveData<List<Bitmap>>()
    val liveDateBitmapImages: LiveData<List<Bitmap>>
        get() = _liveDateBitmapImages

    private var _imageSize = MutableLiveData<Int>()
    val imageSizeLiveData: LiveData<Int>
        get() = _imageSize

    private var _tags = MutableLiveData<List<String>>()
    val tagsLiveData: LiveData<List<String>>
        get() = _tags

    fun addImageAndDiscoverPostToFirebase(
        images: MutableList<ByteArray>,
        postModel: DiscoverPostModel,
        uploadedImages: MutableList<String> = mutableListOf()
    ) {
        if (postModel.postId == null) {
            postModel.postId = UUID.randomUUID().toString()
        }

        postModel.postId?.let { postId ->
            if (images.size > 0) {
                val image = images[0]
                _firebaseMessage.value = Resource.loading(true)
                firebaseRepo.addDiscoverPostImage(image, userId, postId)
                    .addOnSuccessListener { task ->
                        task.storage.downloadUrl.addOnSuccessListener { uri ->
                            images.removeAt(0)
                            uploadedImages.add(uri.toString())
                            addImageAndDiscoverPostToFirebase(images, postModel, uploadedImages)
                        }.addOnFailureListener {
                            _firebaseMessage.value = it.localizedMessage?.let { message ->
                                _firebaseMessage.value = Resource.loading(false)
                                Resource.error("Fotoğraf url alınamadı.\nHata: $message", null)
                            }
                        }
                    }.addOnFailureListener {
                        _firebaseMessage.value = it.localizedMessage?.let { message ->
                            _firebaseMessage.value = Resource.loading(false)
                            Resource.error("Fotoğraf yüklenemedi.\nHata: $message", null)
                        }
                    }
            } else {
                postModel.images = uploadedImages
                postModel.postOwner = userId
                postModel.datePosted = getCurrentTime()
                uploadDiscoverPostToFirestore(postModel)
            }
        }
    }

    private fun uploadDiscoverPostToFirestore(postModel: DiscoverPostModel) {
        _firebaseMessage.value = Resource.loading(true)
        firebaseRepo.uploadDiscoverPostToFirestore(postModel).addOnCompleteListener { task ->
            _firebaseMessage.value = Resource.loading(false)
            if (task.isSuccessful) {
                _firebaseMessage.value = Resource.success(true)
            } else {
                _firebaseMessage.value = task.exception?.localizedMessage?.let { message ->
                    Resource.error(message, false)
                }
            }
        }
    }

    private fun getCurrentTime(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date(currentTime)
        return dateFormat.format(date)
    }

    fun setBitmapImages(newList: List<Bitmap>) = viewModelScope.launch {
        _liveDateBitmapImages.value = newList.toList()
        _imageSize.value = newList.size
    }

    fun setTags(newTags: List<String>) {
        _tags.value = newTags
    }
}