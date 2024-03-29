package com.androiddevelopers.freelanceapp.viewmodel.employer

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.androiddevelopers.freelanceapp.util.snackbar
import com.androiddevelopers.freelanceapp.util.toEmployerJobPost
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreateJobPostingViewModel
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

    private var _imageSize = MutableLiveData<Int>()
    val imageSize: LiveData<Int>
        get() = _imageSize

    private var _firebaseLiveData = MutableLiveData<EmployerJobPost>()
    val firebaseLiveData: LiveData<EmployerJobPost>
        get() = _firebaseLiveData

    fun addImageAndEmployerPostToFirebase(
        images: MutableList<Uri>,
        jobPost: EmployerJobPost,
        uploadedImages: MutableList<String> = mutableListOf()
    ) {
        if (jobPost.postId == null) {
            jobPost.postId = UUID.randomUUID().toString()
        }

        jobPost.postId?.let { postId ->
            if (images.size > 0) {
                val uri = images[0]
                if (uri.toString().contains("firebasestorage")) {
                    images.removeAt(0)
                    uploadedImages.add(uri.toString())
                    addImageAndEmployerPostToFirebase(images, jobPost, uploadedImages)
                } else {
                    _firebaseMessage.value = Resource.loading(true)
                    firebaseRepo.addEmployerPostImage(uri, userId, postId)
                        .addOnSuccessListener { task ->
                            task.storage.downloadUrl.addOnSuccessListener { uri ->
                                images.removeAt(0)
                                uploadedImages.add(uri.toString())
                                addImageAndEmployerPostToFirebase(images, jobPost, uploadedImages)
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
                jobPost.images = uploadedImages
                jobPost.employerId = userId
                addEmployerPostToFirebase(jobPost)
            }
        }
    }

    fun addEmployerPostToFirebase(jobPost: EmployerJobPost) = viewModelScope.launch {
        if (jobPost.postId.isNullOrBlank()) {
            jobPost.postId = UUID.randomUUID().toString()
        }

        _firebaseMessage.value = Resource.loading(true)
        firebaseRepo.addEmployerPostToFirestore(jobPost).addOnCompleteListener { task ->
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

    fun getEmployerJobPostWithDocumentByIdFromFirestore(documentId: String) =
        viewModelScope.launch {
            _firebaseMessage.value = Resource.loading(true)

            firebaseRepo.getEmployerJobPostWithDocumentByIdFromFirestore(documentId)
                .addOnSuccessListener { document ->
                    document.toEmployerJobPost()?.let {
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

//    private fun updateUserData(jobPost: EmployerJobPost) {
//        try {
//            firebaseRepo.uploadDataInUserNode(
//                firebaseAuth.currentUser?.uid.toString(),
//                jobPost,
//                "job_post",
//                jobPost.postId.toString()
//            )
//        } catch (e: Exception) {
//            println("error : " + e.localizedMessage)
//        }
//    }

}