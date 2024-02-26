package com.androiddevelopers.freelanceapp.viewmodel.discover

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.notification.InAppNotificationModel
import com.androiddevelopers.freelanceapp.model.notification.NotificationData
import com.androiddevelopers.freelanceapp.model.notification.PushNotification
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.repo.RoomUserDatabaseRepoInterface
import com.androiddevelopers.freelanceapp.util.Resource
import com.androiddevelopers.freelanceapp.viewmodel.BaseNotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DiscoverDetailsViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    auth: FirebaseAuth
): BaseNotificationViewModel(firebaseRepo,auth) {

    private var _message = MutableLiveData<Resource<UserModel>>()
    val message: LiveData<Resource<UserModel>>
        get() = _message

    private val _discoverPosts = MutableLiveData<List<DiscoverPostModel>>()
    val discoverPosts: LiveData<List<DiscoverPostModel>>
        get() = _discoverPosts

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

    fun likePost(postOwnersToken : String,imageUrl : String,postId : String,likeList: List<String>,userId : String) = GlobalScope.launch(Dispatchers.IO){
            delay(1000)
            val mutableList = mutableSetOf<String>()
            mutableList.addAll(likeList)
            mutableList.add(currentUserId)
            val likeData = hashMapOf<String,Any?>(
                "likeCount" to mutableList.toList()
            )
            firebaseRepo.likePost(postId,likeData).addOnSuccessListener {
                sendNotification(
                    InAppNotificationModel(
                        userId = userId,
                        notificationId = UUID.randomUUID().toString(),
                        title = "Yeni Bir Beğeni",
                        message = "${currentUserData.value?.fullName}, gönderinizi beğendi.",
                        userImage = "${currentUserData.value?.profileImageUrl}",
                        imageUrl = imageUrl,
                        userToken = postOwnersToken
                    ).also {
                        firebaseRepo.saveNotification(it)
                    }
                )
            }
    }
    fun dislikePost(postId : String,likeList: List<String>) = GlobalScope.launch(Dispatchers.IO){
        delay(1000)
        val mutableList = mutableSetOf<String>()
        mutableList.addAll(likeList)
        mutableList.remove(currentUserId)
        val likeData = hashMapOf<String,Any?>(
            "likeCount" to mutableList.toList()
        )
        firebaseRepo.likePost(postId,likeData)
    }
}