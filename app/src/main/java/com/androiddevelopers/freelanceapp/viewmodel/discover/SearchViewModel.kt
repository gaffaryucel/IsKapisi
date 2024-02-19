package com.androiddevelopers.freelanceapp.viewmodel.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.JobStatus
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel  @Inject constructor(
    private val repo: FirebaseRepoInterFace,
    private val auth : FirebaseAuth,
): ViewModel() {
    private val userId = auth.currentUser!!.uid

    //ana değişkenler
    private var _users = MutableLiveData<List<UserModel>>()
    val users: LiveData<List<UserModel>>
        get() = _users

    private var _employerJobPosts = MutableLiveData<List<EmployerJobPost>>()
    val employerJobPosts: LiveData<List<EmployerJobPost>>
        get() = _employerJobPosts

    private var _discoverPosts = MutableLiveData<List<DiscoverPostModel>>()
    val discoverPosts: LiveData<List<DiscoverPostModel>>
        get() = _discoverPosts

    private var _freelancerJobPosts = MutableLiveData<List<FreelancerJobPost>>()
    val freelancerJobPosts: LiveData<List<FreelancerJobPost>>
        get() = _freelancerJobPosts


    //Search Results
    private var _userSearchResult = MutableLiveData<List<UserModel>>()
    val userSearchResult : LiveData<List<UserModel>>
        get() = _userSearchResult

    private var _discoverSearchResult = MutableLiveData<List<DiscoverPostModel>>()
    val discoverSearchResult : LiveData<List<DiscoverPostModel>>
        get() = _discoverSearchResult

    private var _freelancerSearchResult = MutableLiveData<List<FreelancerJobPost>>()
    val freelancerSearchResult : LiveData<List<FreelancerJobPost>>
        get() = _freelancerSearchResult

    private var _employerSearchResults = MutableLiveData<List<EmployerJobPost>>()
    val employerSearchResults : LiveData<List<EmployerJobPost>>
        get() = _employerSearchResults



    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage : LiveData<Resource<Boolean>>
        get() = _firebaseMessage


    init {
        getUsers()
        getAllDiscoverPostsFromFirestore()
        getAllFreelanceJobPost()
        getAllEmployerJobPost()
    }
    private fun getUsers() = viewModelScope.launch{
        _firebaseMessage.value = Resource.loading(null)
        repo.getUsersFromFirestore().addOnSuccessListener {
            _firebaseMessage.value = Resource.loading(false)
            it?.let { querySnapshot ->
                val list: ArrayList<UserModel> = ArrayList()
                querySnapshot.forEach { queryDocumentSnapshot ->
                    val user = queryDocumentSnapshot.toObject(UserModel::class.java)
                    if (user.userId != userId){
                        list.add(user)
                    }
                }
                _users.value = list
            }
        }.addOnFailureListener {
            _firebaseMessage.value = it.localizedMessage?.let { message ->
                Resource.error(message, null)
            }
        }
    }
    private fun getAllDiscoverPostsFromFirestore() = viewModelScope.launch{
        println("discover")
        _firebaseMessage.value = Resource.loading(null)
        repo.getAllDiscoverPostsFromFirestore()
            .addOnSuccessListener {
                val postList = mutableListOf<DiscoverPostModel>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    println("p : "+document)
                    val post = document.toObject(DiscoverPostModel::class.java)
                    post?.let { postList.add(post) }
                    _firebaseMessage.value = Resource.success(null)
                }
                _discoverPosts.value = postList
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _firebaseMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
    private fun getAllFreelanceJobPost() = viewModelScope.launch{
        _firebaseMessage.value = Resource.loading(true)

        repo.getAllFreelancerJobPostFromFirestore()
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

                    _freelancerJobPosts.value = list
                }

                _firebaseMessage.value = Resource.success(true)
            }.addOnFailureListener {
                _firebaseMessage.value = Resource.loading(false)

                it.localizedMessage?.let { message ->
                    Resource.error(message, false)
                }
            }
    }
    private fun getAllEmployerJobPost() = viewModelScope.launch{
        _firebaseMessage.value = Resource.loading(true)

        repo.getAllEmployerJobPostFromFirestore()
            .addOnSuccessListener {

                _firebaseMessage.value = Resource.loading(false)

                it?.let { querySnapshot ->
                    val list = ArrayList<EmployerJobPost>()

                    for (document in querySnapshot) {
                        val employerJobPost = document.toObject(EmployerJobPost::class.java)
                        if (employerJobPost.status == JobStatus.OPEN) {
                            list.add(employerJobPost)
                        }
                    }
                    _employerJobPosts.value = list
                    _firebaseMessage.value = Resource.success(true)
                }
            }.addOnFailureListener {
                _firebaseMessage.value = Resource.loading(false)
                it.localizedMessage?.let { message ->
                    _firebaseMessage.value = Resource.error(message, false)
                }
            }
    }
    fun searchByUsername(query: String) = viewModelScope.launch{
        delay(500)
        val list = users.value
        _userSearchResult.value = list?.filter { it.username!!.contains(query, ignoreCase = true) }
    }
    fun searchByDiscoverDescription(query: String) = viewModelScope.launch{
        delay(500)
        val list = discoverPosts.value
        _discoverSearchResult.value = list?.filter { it.description!!.contains(query, ignoreCase = true) }
    }
    fun searchByFreelanceJobPostTitle(query: String) = viewModelScope.launch{
        delay(500)
        val list = freelancerJobPosts.value
        _freelancerSearchResult.value = list?.filter { it.title!!.contains(query, ignoreCase = true) }
    }
    fun searchByEmployerJobPostTitle(query: String) = viewModelScope.launch{
        delay(500)
        val list = employerJobPosts.value
        _employerSearchResults.value = list?.filter { it.title!!.contains(query, ignoreCase = true) }
    }
}