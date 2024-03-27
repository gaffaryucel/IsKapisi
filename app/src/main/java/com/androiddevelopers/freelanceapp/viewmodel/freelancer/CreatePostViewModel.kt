package com.androiddevelopers.freelanceapp.viewmodel.freelancer

import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.androiddevelopers.freelanceapp.util.snackbar
import com.androiddevelopers.freelanceapp.util.toFreelancerJobPost
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    firebaseAuth: FirebaseAuth
) : ViewModel() {
    private val userId = firebaseAuth.currentUser?.uid.toString()

    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private var _skills = MutableLiveData<List<String>>()
    val skills: LiveData<List<String>>
        get() = _skills

    private var _imageUriList = MutableLiveData<List<Uri>>()
    val imageUriList: LiveData<List<Uri>>
        get() = _imageUriList

    private var _liveDateBitmapImages = MutableLiveData<List<Bitmap>>()
    val liveDateBitmapImages: LiveData<List<Bitmap>>
        get() = _liveDateBitmapImages

    private var _imageSize = MutableLiveData<Int>()
    val imageSize: LiveData<Int>
        get() = _imageSize

    private var _firebaseLiveData = MutableLiveData<FreelancerJobPost>()
    val firebaseLiveData: LiveData<FreelancerJobPost>
        get() = _firebaseLiveData

    fun addImageAndFreelancerPostToFirebase(
        images: MutableList<ByteArray>,
        post: FreelancerJobPost,
        uploadedImages: MutableList<String> = mutableListOf()
    ) {
        if (post.postId == null) {
            post.postId = UUID.randomUUID().toString()
        }

        post.postId?.let { postId ->
            if (images.size > 0) {
                val image = images[0]
                if (image.toString().contains("firebasestorage")) {
                    images.removeAt(0)
                    uploadedImages.add(image.toString())
                    addImageAndFreelancerPostToFirebase(images, post, uploadedImages)
                } else {
                    _firebaseMessage.value = Resource.loading(true)
                    firebaseRepo.addFreelancerPostImage(image, userId, postId)
                        .addOnSuccessListener { task ->
                            task.storage.downloadUrl.addOnSuccessListener {
                                images.removeAt(0)
                                uploadedImages.add(it.toString())
                                addImageAndFreelancerPostToFirebase(
                                    images, post, uploadedImages
                                )
                            }.addOnFailureListener {
                                _firebaseMessage.value = it.localizedMessage?.let { message ->
                                    _firebaseMessage.value = Resource.loading(false)
                                    Resource.error(message, false)
                                }
                            }
                        }.addOnFailureListener {
                            _firebaseMessage.value = it.localizedMessage?.let { message ->
                                _firebaseMessage.value = Resource.loading(false)
                                Resource.error(message, false)
                            }
                        }
                }
            } else {
                post.images = uploadedImages
                post.freelancerId = userId
                addFreelancerPostToFirebase(post)
            }
        }
    }

    private fun addFreelancerPostToFirebase(post: FreelancerJobPost) = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(true)
        firebaseRepo.addFreelancerJobPostToFirestore(post).addOnCompleteListener { task ->
            _firebaseMessage.value = Resource.loading(false)
            if (task.isSuccessful) {
                _firebaseMessage.value = Resource.success(true)
            } else {
                _firebaseMessage.value = task.exception?.localizedMessage?.let { message ->
                    Resource.error(message, false)
                }
            }
        }
        //updateUserData(jobPost)
    }

    fun deleteEmployerJobPostFromFirestore(postId: String, title: String?, view: View) =
        viewModelScope.launch {
            _firebaseMessage.value = Resource.loading(true)
            firebaseRepo.deleteEmployerJobPostFromFirestore(postId).addOnCompleteListener { task ->
                _firebaseMessage.value = Resource.loading(false)
                if (task.isSuccessful) {
                    _firebaseMessage.value = Resource.success(true)
                    "$title İlanınınz silindi.".snackbar(view)
                } else {
                    _firebaseMessage.value = task.exception?.localizedMessage?.let { message ->
                        Resource.error(message, false)
                    }
                }
            }
        }

    fun setImageUriList(newList: List<Uri>) = viewModelScope.launch {
        _imageUriList.value = newList
        _imageSize.value = newList.size
    }

    fun setSkills(newSkills: List<String>) {
        _skills.value = newSkills
    }

    fun setBitmapImages(newList: List<Bitmap>) = viewModelScope.launch {
        _liveDateBitmapImages.value = newList.toList()
        _imageSize.value = newList.size
    }

    fun getFreelancerJobPostWithDocumentByIdFromFirestore(documentId: String) =
        viewModelScope.launch {
            _firebaseMessage.value = Resource.loading(true)
            firebaseRepo.getFreelancerJobPostWithDocumentByIdFromFirestore(documentId)
                .addOnSuccessListener { document ->
                    document.toFreelancerJobPost()?.let {
                        _firebaseLiveData.value = it
                    } ?: run {
                        _firebaseMessage.value =
                            Resource.error("İlan alınırken hata oluştu.", false)
                    }

                    _firebaseMessage.value = Resource.loading(false)
                    // _firebaseMessage.value = Resource.success(true) // observe deki geri gitme özelliği çalıştırıyor

                }.addOnFailureListener {
                    _firebaseMessage.value = Resource.loading(false)

                    it.localizedMessage?.let { message ->
                        _firebaseMessage.value = Resource.error(message, false)
                    }
                }
        }
}

