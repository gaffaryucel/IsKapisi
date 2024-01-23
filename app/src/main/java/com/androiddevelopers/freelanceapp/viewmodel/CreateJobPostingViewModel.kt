package com.androiddevelopers.freelanceapp.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
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

    private var _imageIndex = MutableLiveData<Int>()
    val imageIndex: LiveData<Int>
        get() = _imageIndex

    private var _imageSize = MutableLiveData<Int>()
    val imageSize: LiveData<Int>
        get() = _imageSize

    fun addImageAndJobPostToFirebase(
        newUriList: ArrayList<Uri>,
        jobPost: EmployerJobPost,
        downloadUriList: ArrayList<String> = arrayListOf()
    ) {
        if (newUriList.size > 0) {
            val uri = newUriList[0]
            _firebaseMessage.value = Resource.loading(true)
            uri.lastPathSegment?.let { file ->
                firebaseRepo.addImageToStorage(uri, file)
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
            jobPost.employerId = firebaseAuth.currentUser?.uid ?: ""
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
        updateUserData(jobPost)
    }

    fun setImageUriList(newList: ArrayList<Uri>) = viewModelScope.launch {
        _imageUriList.value = newList
        _imageSize.value = newList.size
    }

    fun setImageIndex(size: Int) = viewModelScope.launch {
        _imageIndex.value = size
    }

    fun setSkills(newSkills: ArrayList<String>) {
        _skills.value = newSkills
    }
    private fun updateUserData(jobPost : EmployerJobPost){
        try {
            firebaseRepo.uploadDataInUserNode(firebaseAuth.currentUser?.uid.toString(),jobPost,"job_post",jobPost.postId.toString())
        }catch (e : Exception){
            println("error : "+ e.localizedMessage)
        }
    }
}