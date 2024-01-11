package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobPostingsViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace
) : ViewModel() {
    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private var _firebaseLiveData = MutableLiveData<ArrayList<EmployerJobPost>>()
    val firebaseLiveData: LiveData<ArrayList<EmployerJobPost>>
        get() = _firebaseLiveData

    fun getAllEmployerJobPost() = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(true)

        firebaseRepo.getAllEmployerJobPostFromFirestore()
            .addOnSuccessListener {

                _firebaseMessage.value = Resource.loading(false)

                it?.let { querySnapshot ->
                    val list: ArrayList<EmployerJobPost> = ArrayList()

                    querySnapshot.forEach { queryDocumentSnapshot ->
                        list.add(
                            queryDocumentSnapshot.toObject(EmployerJobPost::class.java)
                        )
                    }

                    _firebaseMessage.value = Resource.success(true)

                    _firebaseLiveData.value = list
                }
            }.addOnFailureListener {
                _firebaseMessage.value = Resource.loading(false)

                it.localizedMessage?.let { message ->
                    Resource.error(message, false)
                }
            }
    }


}