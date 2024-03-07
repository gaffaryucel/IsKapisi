package com.androiddevelopers.freelanceapp.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.androiddevelopers.freelanceapp.util.toDiscoverPostModel
import com.androiddevelopers.freelanceapp.util.toEmployerJobPost
import com.androiddevelopers.freelanceapp.util.toFreelancerJobPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    firebaseAuth: FirebaseAuth,
) : BaseProfileViewModel(firebaseRepo, firebaseAuth) {

    private var _profileMessage = MutableLiveData<Resource<UserModel>>()
    val profileMessage: LiveData<Resource<UserModel>>
        get() = _profileMessage

    private val _freelancerJobPosts = MutableLiveData<List<FreelancerJobPost>>()
    val freelanceJobPosts: LiveData<List<FreelancerJobPost>>
        get() = _freelancerJobPosts

    private val _employerJobPosts = MutableLiveData<List<EmployerJobPost>>()
    val employerJobPosts: LiveData<List<EmployerJobPost>>
        get() = _employerJobPosts

    private val _discoverPosts = MutableLiveData<List<DiscoverPostModel>>()
    val discoverPosts: LiveData<List<DiscoverPostModel>>
        get() = _discoverPosts

    private var _followerCount = MutableLiveData<Long>()
    val followerCount = _followerCount

    init {
        getDiscoverPostsFromUser(4)
        getEmployerJobPostsFromUser(4)
        getFreelancerJobPostsFromUser(4)
        getFollowerCount()
    }


    private fun getFreelancerJobPostsFromUser(limit : Long) {
        _profileMessage.value = Resource.loading(null)
        firebaseRepo.getAllFreelancerJobPostsFromUser(currentUserId,limit)
            .addOnSuccessListener {
                val postList = mutableListOf<FreelancerJobPost>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toFreelancerJobPost()?.let { post -> postList.add(post) }
                }
                _freelancerJobPosts.value = postList
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _profileMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }

    private fun getEmployerJobPostsFromUser(limit : Long) {
        _profileMessage.value = Resource.loading(null)
        firebaseRepo.getAllEmployerJobPostsFromUser(currentUserId,limit)
            .addOnSuccessListener {
                val postList = mutableListOf<EmployerJobPost>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toEmployerJobPost()?.let { post -> postList.add(post) }
                }
                _employerJobPosts.value = postList
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _profileMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }

    private fun getDiscoverPostsFromUser(limit : Long) {
        _profileMessage.value = Resource.loading(null)
        firebaseRepo.getAllDiscoverPostsFromUser(currentUserId,limit)
            .addOnSuccessListener {
                val postList = mutableListOf<DiscoverPostModel>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toDiscoverPostModel()?.let { post -> postList.add(post) }
                }
                _discoverPosts.value = postList
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _profileMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }

    private fun getFollowerCount() {
        firebaseRepo.getFollowers(currentUserId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _followerCount.value = snapshot.childrenCount
                }

                override fun onCancelled(error: DatabaseError) {
                    //asdasd
                }

            })
    }


}