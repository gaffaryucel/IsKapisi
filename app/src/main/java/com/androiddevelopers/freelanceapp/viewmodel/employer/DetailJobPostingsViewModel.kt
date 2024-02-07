package com.androiddevelopers.freelanceapp.viewmodel.employer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailJobPostingsViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
) : ViewModel() {
    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private var _firebaseLiveData = MutableLiveData<EmployerJobPost>()
    val firebaseLiveData: LiveData<EmployerJobPost>
        get() = _firebaseLiveData

    private var _firebaseUserData = MutableLiveData<UserModel>()
    val firebaseUserData: LiveData<UserModel>
        get() = _firebaseUserData

    fun getEmployerJobPostWithDocumentByIdFromFirestore(documentId: String) =
        viewModelScope.launch {
            _firebaseMessage.value = Resource.loading(true)

            firebaseRepo.getEmployerJobPostWithDocumentByIdFromFirestore(documentId)
                .addOnSuccessListener { document ->
                    val employerJobPost = document.toObject(EmployerJobPost::class.java)

                    employerJobPost?.let {
                        _firebaseLiveData.value = it
                    } ?: run {
                        _firebaseMessage.value =
                            Resource.error("İlan alınırken hata oluştu.", false)
                    }

                    _firebaseMessage.value = Resource.loading(false)
                    _firebaseMessage.value = Resource.success(true)

                }.addOnFailureListener {
                    _firebaseMessage.value = Resource.loading(false)

                    it.localizedMessage?.let { message ->
                        _firebaseMessage.value = Resource.error(message, false)
                    }
                }
        }

    fun getUserDataByDocumentId(documentId: String) =
        viewModelScope.launch {
            _firebaseMessage.value = Resource.loading(true)

            firebaseRepo.getUserDataByDocumentId(documentId)
                .addOnSuccessListener { document ->
                    val userModel = document.toObject(UserModel::class.java)

                    userModel?.let {
                        _firebaseUserData.value = it
                    } ?: run {
                        _firebaseMessage.value =
                            Resource.error("Bu hesapla eşleşen kullanıcı bulunamadı", null)
                    }

                    _firebaseMessage.value = Resource.loading(false)
                    _firebaseMessage.value = Resource.success(true)

                }.addOnFailureListener {
                    _firebaseMessage.value = Resource.loading(false)

                    it.localizedMessage?.let { message ->
                        Resource.error(message, false)
                    }
                }
        }
}