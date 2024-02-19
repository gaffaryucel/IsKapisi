package com.androiddevelopers.freelanceapp.viewmodel.employer

import android.content.SharedPreferences
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class JobPostingsViewModel
@Inject
constructor(
    firebaseRepo: FirebaseRepoInterFace,
    sharedPreferences: SharedPreferences,
) : BaseJobPostingViewModel(firebaseRepo, sharedPreferences) {

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
}