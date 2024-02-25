package com.androiddevelopers.freelanceapp.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.repo.RoomUserDatabaseRepoInterface
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth,
    private val roomRepo: RoomUserDatabaseRepoInterface,
): BaseProfileViewModel(firebaseRepo,roomRepo,firebaseAuth) {
    private val userId = firebaseAuth.currentUser!!.uid

    val isProfileVerified = MutableLiveData<String>()

    private var _profileMessage = MutableLiveData<Resource<UserModel>>()
    val profileMessage : LiveData<Resource<UserModel>>
        get() = _profileMessage

    private val _freelancerJobPosts = MutableLiveData<List<FreelancerJobPost>>()
    val freelanceJobPosts : LiveData<List<FreelancerJobPost>>
        get() = _freelancerJobPosts

    private val _employerJobPosts = MutableLiveData<List<EmployerJobPost>>()
    val employerJobPosts : LiveData<List<EmployerJobPost>>
        get() = _employerJobPosts

    private val _discoverPosts = MutableLiveData<List<DiscoverPostModel>>()
    val discoverPosts : LiveData<List<DiscoverPostModel>>
        get() = _discoverPosts

    private var _savedUserData = roomRepo.observeUserData()
    val savedUserData = _savedUserData

    private var _followerCount = MutableLiveData<Long>()
    val followerCount = _followerCount

    init {
        getDiscoverPostsFromUser()
        getEmployerJobPostsFromUser()
        getFreelancerJobPostsFromUser()
        getFollowerCount()
    }


    private fun getFreelancerJobPostsFromUser(){
        _profileMessage.value = Resource.loading(null)
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
                _profileMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
    private fun getEmployerJobPostsFromUser(){
        _profileMessage.value = Resource.loading(null)
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
                _profileMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
    private fun getDiscoverPostsFromUser(){
        _profileMessage.value = Resource.loading(null)
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
                _profileMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }

    private fun getFollowerCount(){
        firebaseRepo.getFollowers(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _followerCount.value = snapshot.childrenCount
            }

            override fun onCancelled(error: DatabaseError) {
                //asdasd
            }

        })
    }

}