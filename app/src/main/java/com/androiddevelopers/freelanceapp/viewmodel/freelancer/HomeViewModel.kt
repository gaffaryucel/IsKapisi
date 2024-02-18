package com.androiddevelopers.freelanceapp.viewmodel.freelancer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.JobStatus
import com.androiddevelopers.freelanceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace
) : ViewModel() {

    private val _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private val _firebaseLiveData = MutableLiveData<List<FreelancerJobPost>>()
    val firebaseLiveData: LiveData<List<FreelancerJobPost>>
        get() = _firebaseLiveData

    private val _firebaseUserLiveData = MutableLiveData<UserModel>()
    val firebaseUserLiveData: LiveData<UserModel>
        get() = _firebaseUserLiveData


    init {
        getAllFreelanceJobPost()
    }

    private fun getAllFreelanceJobPost() = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(true)

        firebaseRepo.getAllFreelancerJobPostFromFirestore()
            .addOnSuccessListener {
                _firebaseMessage.value = Resource.loading(false)

                it?.let { querySnapshot ->
                    val list = ArrayList<FreelancerJobPost>()

                    for (document in querySnapshot) {
                        val freelancerJobPost = document.toObject(FreelancerJobPost::class.java)
                        if (freelancerJobPost.status == JobStatus.OPEN) {
                            list.add(freelancerJobPost)
                        }
                    }

                    _firebaseLiveData.value = list
                }

                _firebaseMessage.value = Resource.success(true)
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
    ) = viewModelScope.launch {
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

    fun updateLikeFreelancerJobPostFromFirestore(
        userId: String,
        postId: String,
        isLiked: Boolean,
        likes: List<String>
    ) = viewModelScope.launch {
        val list = mutableSetOf<String>()
        list.addAll(likes)

        if (isLiked) {
            list.add(userId)
        } else {
            list.remove(userId)
        }

        firebaseRepo.updateLikeFreelancerJobPostFromFirestore(postId, list.toList())
            .addOnCompleteListener {
                _firebaseMessage.value = Resource.loading(false)
                if (it.isSuccessful) {
                    getAllFreelanceJobPost()
                    _firebaseMessage.value = Resource.success(true)
                } else {
                    _firebaseMessage.value = Resource.loading(false)
                    it.exception?.localizedMessage?.let { message ->
                        _firebaseMessage.value = Resource.error(message, false)
                    }
                }
            }
    }

    fun updateSavedUsersFreelancerJobPostFromFirestore(
        userId: String,
        postId: String,
        isSavedPost: Boolean,
        savedUsers: List<String>
    ) = viewModelScope.launch {
        val list = mutableSetOf<String>()
        list.addAll(savedUsers)

        if (isSavedPost) {
            list.add(userId)
        } else {
            list.remove(userId)
        }

        firebaseRepo.updateSavedUsersFreelancerJobPostFromFirestore(postId, list.toList())
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

    fun getUserDataByDocumentId(userId: String) = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(true)
        firebaseRepo.getUserDataByDocumentId(userId).addOnSuccessListener {
            val userModel = it.toObject(UserModel::class.java)
            userModel?.let { user ->
                _firebaseUserLiveData.value = user
            }
            _firebaseMessage.value = Resource.loading(false)
            _firebaseMessage.value = Resource.success(true)

        }.addOnFailureListener { e ->
            _firebaseMessage.value = Resource.loading(false)
            e.localizedMessage?.let { message ->
                _firebaseMessage.value = Resource.error(message, false)
            }
        }
    }
}