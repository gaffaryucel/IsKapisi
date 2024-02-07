package com.androiddevelopers.freelanceapp.viewmodel.freelancer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.JobStatus
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    firebaseAuth: FirebaseAuth
) : ViewModel() {

    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private var _firebaseLiveData = MutableLiveData<List<FreelancerJobPost>>()
    val firebaseLiveData: LiveData<List<FreelancerJobPost>>
        get() = _firebaseLiveData

    private var _liveDataFirebaseUser = MutableLiveData<FirebaseUser>()
    val liveDataFirebaseUser: LiveData<FirebaseUser>
        get() = _liveDataFirebaseUser

    init {
        getAllFreelanceJobPost()
        _liveDataFirebaseUser.value = firebaseAuth.currentUser
    }

    fun getAllFreelanceJobPost() = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(true)

        firebaseRepo.getAllFreelancerJobPostFromFirestore()
            .addOnSuccessListener {

                _firebaseMessage.value = Resource.loading(false)

                it?.let { querySnapshot ->
                    val list = ArrayList<FreelancerJobPost>()
                    querySnapshot.forEach { document ->
                        val freelancerJobPost = document.toObject(FreelancerJobPost::class.java)
                        if (freelancerJobPost.status == JobStatus.OPEN) {
                            list.add(freelancerJobPost)
                        }
                        _firebaseLiveData.value = list
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

    fun updateViewCountFreelancerJobPostWithDocumentById(
        postId: String,
        newCount: MutableSet<String>
    ) {
        val list = arrayListOf<String>()
        list.addAll(newCount)

        _firebaseMessage.value = Resource.loading(true)
        firebaseRepo.updateViewCountFreelancerJobPostWithDocumentById(postId, list)
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