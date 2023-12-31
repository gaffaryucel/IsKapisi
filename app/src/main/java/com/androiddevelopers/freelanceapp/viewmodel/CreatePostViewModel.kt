package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel  @Inject constructor(
    private val repository: FirebaseRepoInterFace // JobPostRepository'nin injekte edilmesi
) : ViewModel() {

    private var _insertPostMessage = MutableLiveData<Resource<Boolean>>()
    val insertPostMessage : LiveData<Resource<Boolean>>
        get() = _insertPostMessage

    private fun checkIsPostValid(jobPost: FreelancerJobPost): Boolean {
        return jobPost.postId != null &&
            jobPost.freelancerId != null &&
            jobPost.title != null &&
            jobPost.description != null &&
            jobPost.images != null &&
            jobPost.skillsRequired != null &&
            jobPost.budget != null &&
            jobPost.deadline != null &&
            jobPost.location != null &&
            jobPost.datePosted != null &&
            jobPost.applicants != null &&
            jobPost.status != null &&
            jobPost.additionalDetails != null &&
            jobPost.viewCount != null &&
            jobPost.isUrgent != null
    }
    fun uploadJobPost(jobPost: FreelancerJobPost) = viewModelScope.launch{
        // Job post'un Firestore'a yüklenmesi
        _insertPostMessage.value = Resource.loading(null)
        if (checkIsPostValid(jobPost)){
            repository.addFreelancerJobPostToFirestore(jobPost)
                .addOnSuccessListener {
                    _insertPostMessage.value = Resource.success(null)
                }.addOnFailureListener { e ->
                    _insertPostMessage.value = Resource.error(e.localizedMessage ?: "error : try again later",null)
                }
        }else{
            _insertPostMessage.value = Resource.error("Lütfen tüm alanları doldurun",null)
        }
    }
}