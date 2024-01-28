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
class HomeViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
) : ViewModel() {

    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private var _firebaseLiveData = MutableLiveData<List<FreelancerJobPost>>()
    val firebaseLiveData: LiveData<List<FreelancerJobPost>>
        get() = _firebaseLiveData

    init {
        getAllFreelanceJobPost()
    }

    fun getAllFreelanceJobPost() = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(true)

        firebaseRepo.getAllFreelancerJobPostFromFirestore()
            .addOnSuccessListener {

                _firebaseMessage.value = Resource.loading(false)

                it?.let { querySnapshot ->
                    _firebaseLiveData.value = querySnapshot.map { document ->
                        document.toObject(FreelancerJobPost::class.java)
                    }

                    _firebaseMessage.value = Resource.success(true)
                }
            }.addOnFailureListener {
                _firebaseMessage.value = Resource.loading(false)

                it.localizedMessage?.let { message ->
                    Resource.error(message, false)
                }
            }
    }
}