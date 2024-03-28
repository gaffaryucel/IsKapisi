package com.androiddevelopers.freelanceapp.viewmodel.freelancer

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

    private var _imageSize = MutableLiveData<Int>()
    val imageSize: LiveData<Int>
        get() = _imageSize

    private var _firebaseLiveData = MutableLiveData<FreelancerJobPost>()
    val firebaseLiveData: LiveData<FreelancerJobPost>
        get() = _firebaseLiveData

    fun addImageAndFreelancerPostToFirebase(
        images: MutableList<Uri>,
        post: FreelancerJobPost,
        uploadedImages: MutableList<String> = mutableListOf()
    ) {
        if (post.postId == null) {
            post.postId = UUID.randomUUID().toString()
        }

        post.postId?.let { postId ->
            if (images.size > 0) {
                val uri = images[0]
                if (uri.toString().contains("firebasestorage")) {
                    images.removeAt(0)
                    uploadedImages.add(uri.toString())
                    addImageAndFreelancerPostToFirebase(images, post, uploadedImages)
                } else {
                    _firebaseMessage.value = Resource.loading(true)
                    firebaseRepo.addFreelancerPostImage(uri, userId, postId)
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

//    fun uploadPostPicture(postModel: FreelancerJobPost, r: ByteArray) = viewModelScope.launch {
//        _uploadPhotoMessage.value = Resource.loading("loading")
//
//        val photoFileName = "${UUID.randomUUID()}.jpg"
//        val photoRef =
//            storageReference.child("users/${currentUserId}/freelancer_post_photo/$photoFileName")
//
//        photoRef.putBytes(r)
//            .addOnSuccessListener {
//                photoRef.downloadUrl
//                    .addOnSuccessListener { uri ->
//                        val imageUrl = uri.toString()
//                        postModel.images = listOf(imageUrl)
//                        uploadJobPost(postModel)
//                    }
//                    .addOnFailureListener { exception ->
//                        // URL alınamazsa burada hata işleme kodlarınızı yazabilirsiniz.
//                        _uploadPhotoMessage.value =
//                            Resource.error("cannot acces url", exception.localizedMessage)
//                    }
//            }.addOnFailureListener { exception ->
//                // Yükleme başarısız olursa, burada hata işleme kodlarınızı yazabilirsiniz.
//                _uploadPhotoMessage.value =
//                    Resource.error("cannot upload photo", exception.localizedMessage)
//            }
//    }
//
//    private fun uploadJobPost(jobPost: FreelancerJobPost) = viewModelScope.launch {
//        // Job post'un Firestore'a yüklenmesi
//        _insertPostMessage.value = Resource.loading(null)
//        if (checkIsPostValid(jobPost)) {
//            repo.addFreelancerJobPostToFirestore(jobPost)
//                .addOnSuccessListener {
//                    _insertPostMessage.value = Resource.success(null)
//                    updateUserData(jobPost)
//                }.addOnFailureListener { e ->
//                    _insertPostMessage.value =
//                        Resource.error(e.localizedMessage ?: "error : try again later", null)
//                }
//        } else {
//            _insertPostMessage.value = Resource.error("Lütfen tüm alanları doldurun", null)
//        }
//    }
//
//    private fun getCurrentTime(): String {
//        val currentTime = System.currentTimeMillis()
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//        val date = Date(currentTime)
//        return dateFormat.format(date)
//    }

//    private fun updateUserData(jobPost: FreelancerJobPost) {
//        repo.uploadDataInUserNode(
//            currentUserId.toString(),
//            jobPost,
//            "freelancer_job_post",
//            jobPost.postId.toString()
//        )
//    }

//    private fun checkIsPostValid(jobPost: FreelancerJobPost): Boolean {
//        return jobPost.postId != null &&
//                jobPost.freelancerId != null &&
//                jobPost.title != null &&
//                jobPost.description != null &&
//                jobPost.images != null &&
//                jobPost.skillsRequired != null &&
//                jobPost.budget != null &&
//                jobPost.deadline != null &&
//                jobPost.location != null &&
//                jobPost.datePosted != null &&
//                jobPost.applicants != null &&
//                jobPost.status != null &&
//                jobPost.additionalDetails != null &&
//                jobPost.viewCount != null &&
//                jobPost.isUrgent != null
//    }
}

