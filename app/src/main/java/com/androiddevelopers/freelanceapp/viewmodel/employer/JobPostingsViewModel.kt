package com.androiddevelopers.freelanceapp.viewmodel.employer

import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class JobPostingsViewModel @Inject constructor(firebaseRepo: FirebaseRepoInterFace) :
    BaseJobPostingViewModel(firebaseRepo) {
//    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
//    val firebaseMessage: LiveData<Resource<Boolean>>
//        get() = _firebaseMessage
//
//    private var _firebaseLiveData = MutableLiveData<List<EmployerJobPost>>()
//    val firebaseLiveData: LiveData<List<EmployerJobPost>>
//        get() = _firebaseLiveData

    init {
        getAllEmployerJobPost()
    }

    fun updateViewCountEmployerJobPostWithDocumentById(
        postId: String,
        newCount: MutableSet<String>
    ) {
        val list = arrayListOf<String>()
        list.addAll(newCount)

        _firebaseMessage.value = Resource.loading(true)
        firebaseRepo.updateViewCountEmployerJobPostWithDocumentById(postId, list)
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

//    fun updateSavedUsersEmployerJobPostFromFirestore(
//        userId: String,
//        postId: String,
//        isSavedPost: Boolean,
//        savedUsers: List<String>
//    ) = viewModelScope.launch {
//        val list = mutableSetOf<String>()
//        list.addAll(savedUsers)
//
//        if (isSavedPost) {
//            list.add(userId)
//        } else {
//            list.remove(userId)
//        }
//
//        firebaseRepo.updateSavedUsersEmployerJobPostFromFirestore(postId, list.toList())
//            .addOnCompleteListener {
//                _firebaseMessage.value = Resource.loading(false)
//                if (it.isSuccessful) {
//                    _firebaseMessage.value = Resource.success(true)
//                } else {
//                    _firebaseMessage.value = Resource.loading(false)
//                    it.exception?.localizedMessage?.let { message ->
//                        _firebaseMessage.value = Resource.error(message, false)
//                    }
//                }
//            }
//    }
}