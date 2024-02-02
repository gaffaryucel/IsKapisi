package com.androiddevelopers.freelanceapp.viewmodel.employer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.JobStatus
import com.androiddevelopers.freelanceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobPostingsViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
) : ViewModel() {
    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private var _firebaseLiveData = MutableLiveData<List<EmployerJobPost>>()
    val firebaseLiveData: LiveData<List<EmployerJobPost>>
        get() = _firebaseLiveData

    init {
        getAllEmployerJobPost()
    }

    private fun getAllEmployerJobPost() = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(true)

        firebaseRepo.getAllEmployerJobPostFromFirestore()
            .addOnSuccessListener {

                _firebaseMessage.value = Resource.loading(false)

                it?.let { querySnapshot ->
                    val list = ArrayList<EmployerJobPost>()

                    for (document in querySnapshot) {
                        val employerJobPost = document.toObject(EmployerJobPost::class.java)
                        if (employerJobPost.status == JobStatus.OPEN) {
                            list.add(employerJobPost)
                        }
                    }
                    _firebaseLiveData.value = list

                    _firebaseMessage.value = Resource.success(true)

                }
            }.addOnFailureListener {
                _firebaseMessage.value = Resource.loading(false)

                it.localizedMessage?.let { message ->
                    _firebaseMessage.value = Resource.error(message, false)
                }
            }
    }

    fun updateViewCountEmployerJobPostWithDocumentById(postId: String, newCount: Int) {
        _firebaseMessage.value = Resource.loading(true)
        firebaseRepo.updateViewCountEmployerJobPostWithDocumentById(postId, newCount)
            .addOnCompleteListener {
                _firebaseMessage.value = Resource.loading(false)
                if (it.isSuccessful) {
                    _firebaseMessage.value = Resource.success(true)
                } else {
                    _firebaseMessage.value = Resource.loading(false)
                    it.exception?.localizedMessage?.let { message ->
                        _firebaseMessage.value = Resource.error(message, false)
                    }
                }
            }
    }
}