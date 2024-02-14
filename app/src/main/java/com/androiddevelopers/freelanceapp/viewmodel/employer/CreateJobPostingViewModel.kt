package com.androiddevelopers.freelanceapp.viewmodel.employer

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.androiddevelopers.freelanceapp.util.snackbar
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
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

    private var _imageSize = MutableLiveData<Int>()
    val imageSize: LiveData<Int>
        get() = _imageSize

    private var _firebaseLiveData = MutableLiveData<EmployerJobPost>()
    val firebaseLiveData: LiveData<EmployerJobPost>
        get() = _firebaseLiveData

    fun addImageAndJobPostToFirebase(
        newUriList: ArrayList<Uri>,
        jobPost: EmployerJobPost,
        downloadUriList: ArrayList<String> = arrayListOf()
    ) {
        val uId = firebaseAuth.currentUser?.uid.toString()
        if (newUriList.size > 0) {
            val uri = newUriList[0]
            if (uri.toString().contains("firebasestorage")) {
                newUriList.removeAt(0)
                downloadUriList.add(uri.toString())
                addImageAndJobPostToFirebase(newUriList, jobPost, downloadUriList)
            } else {
                _firebaseMessage.value = Resource.loading(true)
                uri.lastPathSegment?.let { file ->
                    firebaseRepo.addImageToStorageForJobPosting(uri, uId, jobPost.postId!!, file)
                        .addOnSuccessListener { task ->
                            task.storage.downloadUrl
                                .addOnSuccessListener {
                                    newUriList.removeAt(0)
                                    downloadUriList.add(it.toString())
                                    addImageAndJobPostToFirebase(
                                        newUriList,
                                        jobPost,
                                        downloadUriList
                                    )
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
            }
        } else {
            if (jobPost.postId == null) {
                jobPost.postId = UUID.randomUUID().toString()
            }
            jobPost.images = downloadUriList
            jobPost.employerId = uId
            addJobPostingToFirebase(jobPost)
        }
    }

    private fun addJobPostingToFirebase(jobPost: EmployerJobPost) = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(true)
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
        //updateUserData(jobPost)
    }

    fun deleteEmployerJobPostFromFirestore(postId: String, title: String?, view: View) =
        viewModelScope.launch {
            _firebaseMessage.value = Resource.loading(true)
            firebaseRepo.deleteEmployerJobPostFromFirestore(postId).addOnCompleteListener { task ->
                _firebaseMessage.value = Resource.loading(false)
                if (task.isSuccessful) {
                    _firebaseMessage.value = Resource.success(true)
                    "$title İlanınınz silindi.".snackbar(view)
                } else {
                    _firebaseMessage.value =
                        task.exception?.localizedMessage?.let { message ->
                            Resource.error(message, false)
                        }
                }
            }
        }

    fun setImageUriList(newList: ArrayList<Uri>) = viewModelScope.launch {
        _imageUriList.value = newList
        _imageSize.value = newList.size
    }


    fun setSkills(newSkills: ArrayList<String>) {
        _skills.value = newSkills
    }

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
                    // _firebaseMessage.value = Resource.success(true) // observe deki geri gitme özelliği çalıştırıyor

                }.addOnFailureListener {
                    _firebaseMessage.value = Resource.loading(false)

                    it.localizedMessage?.let { message ->
                        _firebaseMessage.value = Resource.error(message, false)
                    }
                }
        }

//    private fun updateUserData(jobPost: EmployerJobPost) {
//        try {
//            firebaseRepo.uploadDataInUserNode(
//                firebaseAuth.currentUser?.uid.toString(),
//                jobPost,
//                "job_post",
//                jobPost.postId.toString()
//            )
//        } catch (e: Exception) {
//            println("error : " + e.localizedMessage)
//        }
//    }

}