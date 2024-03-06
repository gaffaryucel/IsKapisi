package com.androiddevelopers.freelanceapp.viewmodel.freelancer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val repo: FirebaseRepoInterFace,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val storageReference = storage.reference
    private val _uploadPhotoMessage = MutableLiveData<Resource<String>>()
    val uploadPhotoMessage: LiveData<Resource<String>> = _uploadPhotoMessage

    private val currentUserId = auth.currentUser?.uid.toString()

    private var _insertPostMessage = MutableLiveData<Resource<Boolean>>()
    val insertPostMessage: LiveData<Resource<Boolean>>
        get() = _insertPostMessage

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

