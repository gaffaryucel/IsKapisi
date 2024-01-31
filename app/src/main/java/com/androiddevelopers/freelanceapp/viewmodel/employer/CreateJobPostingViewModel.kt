package com.androiddevelopers.freelanceapp.viewmodel.employer

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.JobStatus
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateJobPostingViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private var _skills = MutableLiveData<ArrayList<String>>()
    val skills: LiveData<ArrayList<String>>
        get() = _skills

    private var _imageUriList = MutableLiveData<ArrayList<Uri>>()
    val imageUriList: LiveData<ArrayList<Uri>>
        get() = _imageUriList

    private var _imageSize = MutableLiveData<Int>()
    val imageSize: LiveData<Int>
        get() = _imageSize

    fun addImageAndJobPostToFirebase(
        newUriList: ArrayList<Uri>,
        jobPost: EmployerJobPost,
        downloadUriList: ArrayList<String> = arrayListOf()
    ) {
        val uId = firebaseAuth.currentUser?.uid ?: "null_uid"
        if (newUriList.size > 0) {
            val uri = newUriList[0]
            _firebaseMessage.value = Resource.loading(true)
            uri.lastPathSegment?.let { file ->
                firebaseRepo.addImageToStorageForJobPosting(uri, uId, jobPost.postId!!, file)
                    .addOnSuccessListener { task ->
                        task.storage.downloadUrl
                            .addOnSuccessListener {
                                newUriList.removeAt(0)
                                downloadUriList.add(it.toString())
                                addImageAndJobPostToFirebase(newUriList, jobPost, downloadUriList)
                            }.addOnFailureListener {
                                _firebaseMessage.value =
                                    it.localizedMessage?.let { message ->
                                        _firebaseMessage.value = Resource.loading(false)
                                        Resource.error(message, false)
                                    }
                            }
                    }.addOnFailureListener {
                        _firebaseMessage.value =
                            it.localizedMessage?.let { message ->
                                _firebaseMessage.value = Resource.loading(false)
                                Resource.error(message, false)
                            }
                    }
            }
        } else {
            jobPost.images = downloadUriList
            jobPost.employerId = uId
            addJobPostingToFirebase(jobPost)
        }
    }

    private fun addJobPostingToFirebase(jobPost: EmployerJobPost) = viewModelScope.launch {
        //_firebaseMessage.value = Resource.loading(true)
        firebaseRepo.addEmployerJobPostToFirestore(jobPost).addOnCompleteListener { task ->
            _firebaseMessage.value = Resource.loading(false)
            if (task.isSuccessful) {
                _firebaseMessage.value = Resource.success(true)
            } else {
                _firebaseMessage.value =
                    task.exception?.localizedMessage?.let { message ->
                        Resource.error(message, false)
                    }
            }
        }
       //updateUserData(jobPost)
    }

    fun setImageUriList(newList: ArrayList<Uri>) = viewModelScope.launch {
        _imageUriList.value = newList
        _imageSize.value = newList.size
    }


    fun setSkills(newSkills: ArrayList<String>) {
        _skills.value = newSkills
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

    fun createEmployerJobPost(
        postId: String? = "",
        title: String? = "",
        description: String? = "",
        images: List<String>? = listOf(),
        skillsRequired: List<String>? = listOf(),
        budget: Double? = 0.0,
        deadline: String? = "",
        location: String? = "",
        datePosted: String? = "",
        applicants: List<String>? = listOf(),
        status: JobStatus? = JobStatus.OPEN,
        additionalDetails: String? = "",
        completedJobs: Int? = 0,
        canceledJobs: Int? = 0,
        viewCount: Int? = 0,
        isUrgent: Boolean? = false,
        employerId: String? = ""
    ): EmployerJobPost {
        return EmployerJobPost(
            postId = postId,
            title = title,
            description = description,
            images = images,
            skillsRequired = skillsRequired,
            budget = budget,
            deadline = deadline,
            location = location,
            datePosted = datePosted,
            applicants = applicants,
            status = status,
            additionalDetails = additionalDetails,
            completedJobs = completedJobs,
            canceledJobs = canceledJobs,
            viewCount = viewCount,
            isUrgent = isUrgent,
            employerId = employerId,
        )
    }
}