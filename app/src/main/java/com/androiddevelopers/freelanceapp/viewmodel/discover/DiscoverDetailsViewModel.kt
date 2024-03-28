package com.androiddevelopers.freelanceapp.viewmodel.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.notification.InAppNotificationModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.NotificationType
import com.androiddevelopers.freelanceapp.util.NotificationTypeForActions
import com.androiddevelopers.freelanceapp.util.Resource
import com.androiddevelopers.freelanceapp.util.toDiscoverPostModel
import com.androiddevelopers.freelanceapp.util.toUserModel
import com.androiddevelopers.freelanceapp.viewmodel.BaseNotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DiscoverDetailsViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    auth: FirebaseAuth
) : BaseNotificationViewModel(firebaseRepo, auth) {

    private var _message = MutableLiveData<Resource<UserModel>>()
    val message: LiveData<Resource<UserModel>>
        get() = _message

    private val _discoverPosts = MutableLiveData<List<DiscoverPostModel>>()
    val discoverPosts: LiveData<List<DiscoverPostModel>>
        get() = _discoverPosts

    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private var _firebaseUserListData = MutableLiveData<List<UserModel>>()
    val firebaseUserListData: LiveData<List<UserModel>>
        get() = _firebaseUserListData

    init {
        getPosts()
    }

    private fun getPosts() {
        _message.value = Resource.loading(null)
        firebaseRepo.getAllDiscoverPostsFromFirestore()
            .addOnSuccessListener {
                val postList = mutableListOf<DiscoverPostModel>()
                val userIdList = mutableSetOf<String>()
                for (document in it.documents) {
                    document.toDiscoverPostModel()?.let { post ->
                        postList.add(post)
                        post.postOwner?.let { userId ->
                            userIdList.add(userId)
                        }
                    }
                }

                if (userIdList.isNotEmpty()) {
                    getUserDataByDocumentIdList(userIdList.toList())
                }
                _discoverPosts.value = postList
            }
            .addOnFailureListener { exception ->
                _message.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }

    fun likePost(
        postOwnersToken: String,
        imageUrl: String,
        postId: String,
        likeList: List<String>,
    ) = GlobalScope.launch(Dispatchers.IO) {
        delay(1000)
        val mutableList = mutableSetOf<String>()
        mutableList.addAll(likeList)
        mutableList.add(currentUserId)
        val likeData = hashMapOf<String, Any?>(
            "likeCount" to mutableList.toList()
        )
        firebaseRepo.likePost(postId, likeData).addOnSuccessListener {
            val myNotification = createNotification(imageUrl, postOwnersToken, postId)
            sendNotification(
                notification = myNotification,
                type = NotificationTypeForActions.LIKE,
                like = postId
            )
            firebaseRepo.saveNotification(myNotification)
        }
    }

    fun dislikePost(postId: String, likeList: List<String>) = GlobalScope.launch(Dispatchers.IO) {
        delay(1000)
        val mutableList = mutableSetOf<String>()
        mutableList.addAll(likeList)
        mutableList.remove(currentUserId)
        val likeData = hashMapOf<String, Any?>(
            "likeCount" to mutableList.toList()
        )
        firebaseRepo.likePost(postId, likeData)
    }

    private fun createNotification(
        imageUrl: String,
        postOwnersToken: String,
        postId: String
    ) = InAppNotificationModel(
        userId = currentUserId,
        notificationType = NotificationType.LIKE,
        notificationId = UUID.randomUUID().toString(),
        title = "Yeni Bir Beğeni",
        message = "${currentUserData.value?.fullName}, gönderinizi beğendi.",
        userImage = "${currentUserData.value?.profileImageUrl}",
        imageUrl = imageUrl,
        userToken = postOwnersToken,
        time = getCurrentTime(),
        idForAction = postId
    )

    private fun getUserDataByDocumentIdList(list: List<String>) = viewModelScope.launch {
        firebaseRepo.getUsersFromFirestore(list).addOnSuccessListener { querySnapshot ->
            val users = mutableListOf<UserModel>()

            for (document in querySnapshot) {
                document.toUserModel()?.let { user ->
                    users.add(user)
                }
            }

            _firebaseUserListData.value = users
            _firebaseMessage.value = Resource.success(true)
        }.addOnFailureListener {
            _firebaseMessage.value = Resource.loading(false)

            it.localizedMessage?.let { message ->
                Resource.error(message, false)
            }
        }
    }
}