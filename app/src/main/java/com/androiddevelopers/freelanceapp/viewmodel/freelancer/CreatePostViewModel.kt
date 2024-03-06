package com.androiddevelopers.freelanceapp.viewmodel.freelancer

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val storage: FirebaseStorage,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val storageReference = storage.reference
    private val _uploadPhotoMessage = MutableLiveData<Resource<String>>()
    val uploadPhotoMessage: LiveData<Resource<String>> = _uploadPhotoMessage

    private val currentUserId = firebaseAuth.currentUser?.uid.toString()

    private var _insertPostMessage = MutableLiveData<Resource<Boolean>>()
    val insertPostMessage: LiveData<Resource<Boolean>>
        get() = _insertPostMessage

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

    fun addImageAndJobPostToFirebase(
        newUriList: MutableList<Uri>,
        post: FreelancerJobPost,
        downloadUriList: MutableList<String> = mutableListOf()
    ) {
        val uId = firebaseAuth.currentUser?.uid.toString()
        if (newUriList.size > 0) {
            val uri = newUriList[0]
            if (uri.toString().contains("firebasestorage")) {
                newUriList.removeAt(0)
                downloadUriList.add(uri.toString())
                addImageAndJobPostToFirebase(newUriList, post, downloadUriList)
            } else {
                _firebaseMessage.value = Resource.loading(true)
                uri.lastPathSegment?.let { file ->
                    firebaseRepo.addImageToStorageForJobPosting(uri, uId, post.postId!!, file)
                        .addOnSuccessListener { task ->
                            task.storage.downloadUrl.addOnSuccessListener {
                                newUriList.removeAt(0)
                                downloadUriList.add(it.toString())
                                addImageAndJobPostToFirebase(
                                    newUriList, post, downloadUriList
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
            }
        } else {
            if (post.postId == null) {
                post.postId = UUID.randomUUID().toString()
            }
            post.images = downloadUriList
            post.freelancerId = uId
            addJobPostingToFirebase(post)
        }
    }

    private fun addJobPostingToFirebase(post: FreelancerJobPost) = viewModelScope.launch {
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

    fun setImageUriList(newList: List<Uri>) = viewModelScope.launch {
        _imageUriList.value = newList
        _imageSize.value = newList.size
    }

    fun setSkills(newSkills: List<String>) {
        _skills.value = newSkills
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

