package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun addJobPostingToFirebase(jobPost: EmployerJobPost) = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(true)
        jobPost.employerId = firebaseAuth.currentUser?.uid ?: ""

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
    }
}