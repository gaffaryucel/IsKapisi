package com.androiddevelopers.freelanceapp.viewmodel.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.UserProfileModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.repo.RoomUserDatabaseRepoInterface
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth,
    private val roomRepo: RoomUserDatabaseRepoInterface,
): ViewModel() {
    private val userId = firebaseAuth.currentUser!!.uid

    private var _message = MutableLiveData<Resource<UserModel>>()
    val message : LiveData<Resource<UserModel>>
        get() = _message

    private val _userData = MutableLiveData<UserProfileModel>()
    val userData : LiveData<UserProfileModel>
        get() = _userData

    private val _allUserData = MutableLiveData<UserModel>()
    val allUserData : LiveData<UserModel>
        get() = _allUserData

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

    init {
        getUserDataFromFirebase()
        getDiscoverPostsFromUser()
        getEmployerJobPostsFromUser()
        getFreelancerJobPostsFromUser()
    }
    fun getUserDataFromFirebase(){
        firebaseRepo.getUserDataByDocumentId(userId)
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(UserModel::class.java)
                    if (user != null) {
                        _allUserData.value = user ?: UserModel()
                        _userData.value = convertToUserProfileModel(user ?: UserModel())
                        _message.value = Resource.success(null)
                        try {
                            isRoomDataExists()
                        }catch (e : Exception){
                            _message.value = Resource.error(e.localizedMessage ?: "error",null)
                        }
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
                println("Belge alınamadı. Hata: $exception")
                _message.value = Resource.error("Belge alınamadı. Hata: $exception",null)
            }
        }
    private fun isRoomDataExists(){
        if (userData.value != savedUserData.value){
            if (savedUserData.value != null){
                updateUser()
            }else{
                insertUser()
            }
        }
    }
    private fun updateUser(){
        try {
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    userData.value?.let {roomRepo.updateUser(it)}
                }
            }
        }catch (e : Exception){
            _message.value = Resource.error(e.localizedMessage ?: "error",null)
        }

    }
    private fun insertUser() {
        try {
            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    userData.value?.let { roomRepo.insertUser(it) }
                }
            }
        }catch (e : Exception){
            _message.value = Resource.error(e.localizedMessage ?: "error",null)
        }
    }
    private fun convertToUserProfileModel(userModel: UserModel): UserProfileModel {
        return UserProfileModel(
            userId = userModel.userId ?: "",
            username = userModel.username ?: "",
            email = userModel.email
        )
    }

    private fun getFreelancerJobPostsFromUser(){
        _message.value = Resource.loading(null)
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
    private fun getEmployerJobPostsFromUser(){
        _message.value = Resource.loading(null)
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
    private fun getDiscoverPostsFromUser(){
        _message.value = Resource.loading(null)
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