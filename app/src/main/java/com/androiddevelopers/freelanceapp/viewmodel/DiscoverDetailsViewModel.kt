package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.VideoModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.repo.RoomUserDatabaseRepoInterface
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverDetailsViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth,
): ViewModel() {

    private var _message = MutableLiveData<Resource<UserModel>>()
    val message: LiveData<Resource<UserModel>>
        get() = _message

    private val _discoverPosts = MutableLiveData<List<DiscoverPostModel>>()
    val discoverPosts: LiveData<List<DiscoverPostModel>>
        get() = _discoverPosts

    private val userId = auth.currentUser?.uid.toString()

    init {
        getPosts()
    }

    private fun getPosts() {
        _message.value = Resource.loading(null)
        firebaseRepo.getAllDiscoverPostsFromFirestore()
            .addOnSuccessListener {
                val postList = mutableListOf<DiscoverPostModel>()
                for (document in it .documents) {
                    val post = document.toObject(DiscoverPostModel::class.java)
                    post?.let { postList.add(it) }
                }
                _discoverPosts.value = postList
            }
            .addOnFailureListener { exception ->
                _message.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }

    fun likePost(postId : String,likeList: List<String>) = viewModelScope.launch{
        val mutableList = mutableListOf<String>()
        mutableList.addAll(likeList)
        mutableList.add(userId)
        val likeData = hashMapOf<String,Any?>(
            "likeCount" to mutableList
        )
        firebaseRepo.likePost(postId,likeData)
    }
    fun dislikePost(postId : String,likeList: List<String>) = viewModelScope.launch{
        val mutableList = mutableListOf<String>()
        mutableList.addAll(likeList)
        mutableList.remove(userId)
        val likeData = hashMapOf<String,Any?>(
            "likeCount" to mutableList
        )
        firebaseRepo.likePost(postId,likeData)
    }
}