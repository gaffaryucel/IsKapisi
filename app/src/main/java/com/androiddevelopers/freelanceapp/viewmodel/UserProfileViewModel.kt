package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.UserProfileModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel  @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace
): ViewModel(){

    private var _message = MutableLiveData<Resource<UserModel>>()
    val message : LiveData<Resource<UserModel>>
        get() = _message

    private val _userData = MutableLiveData<UserModel>()
    val userData : LiveData<UserModel>
        get() = _userData

    private val _freelancerJobPosts = MutableLiveData<List<FreelancerJobPost>>()
    val freelanceJobPosts : LiveData<List<FreelancerJobPost>>
        get() = _freelancerJobPosts

    private val _employerJobPosts = MutableLiveData<List<EmployerJobPost>>()
    val employerJobPosts : LiveData<List<EmployerJobPost>>
        get() = _employerJobPosts

    private val _discoverPosts = MutableLiveData<List<DiscoverPostModel>>()
    val discoverPosts : LiveData<List<DiscoverPostModel>>
        get() = _discoverPosts

    fun getUserDataFromFirebase(userId : String){
        firebaseRepo.getUserDataByDocumentId(userId)
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(UserModel::class.java)
                    if (user != null) {
                        _userData.value = user
                        _message.value = Resource.success(null)
                        getDiscoverPostsFromUser(userId)
                        getEmployerJobPostsFromUser(userId)
                        getFreelancerJobPostsFromUser(userId)
                    }else{
                        _message.value = Resource.error("Belirtilen belge bulunamadı",null)
                    }
                } else {
                    // Belge yoksa işlemleri buraya ekleyebilirsiniz
                    _message.value = Resource.error("kullanıcı kaydedilmemiş",null)
                }
            }
            .addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _message.value = Resource.error("Belge alınamadı. Hata: $exception",null)
            }
    }

    private fun getFreelancerJobPostsFromUser(userId: String){
        firebaseRepo.getAllFreelancerJobPostsFromUser(userId)
            .addOnSuccessListener {
                val postList = mutableListOf<FreelancerJobPost>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    val post = document.toObject(FreelancerJobPost::class.java)
                    post?.let { postList.add(post) }
                }
                _freelancerJobPosts.value = postList
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _message.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
    private fun getEmployerJobPostsFromUser(userId: String){
        firebaseRepo.getAllEmployerJobPostsFromUser(userId)
            .addOnSuccessListener {
                val postList = mutableListOf<EmployerJobPost>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    val post = document.toObject(EmployerJobPost::class.java)
                    post?.let { postList.add(post) }
                }
                _employerJobPosts.value = postList
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _message.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
    private fun getDiscoverPostsFromUser(userId: String){
        firebaseRepo.getAllDiscoverPostsFromUser(userId)
            .addOnSuccessListener {
                val postList = mutableListOf<DiscoverPostModel>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    val post = document.toObject(DiscoverPostModel::class.java)
                    post?.let { postList.add(post) }
                }
                _discoverPosts.value = postList
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _message.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
}