package com.androiddevelopers.freelanceapp.viewmodel.discover

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.CommentModel
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.notification.InAppNotificationModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.NotificationType
import com.androiddevelopers.freelanceapp.util.Resource
import com.androiddevelopers.freelanceapp.viewmodel.BaseNotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CommentsViewModel  @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    auth: FirebaseAuth,
) : BaseNotificationViewModel(firebaseRepo,auth) {

    private var _message = MutableLiveData<Resource<UserModel>>()
    val message: LiveData<Resource<UserModel>>
        get() = _message

    private val _discoverPostComments = MutableLiveData<List<CommentModel>>()
    val discoverPostComments: LiveData<List<CommentModel>>
        get() = _discoverPostComments

    private val _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData

    init {
        getUserDataFromFirebase()
    }
    fun getAllComments(postId : String){
        _message.value = Resource.loading(null)
        firebaseRepo.getDiscoverPostDataFromFirebase(postId)
            .addOnSuccessListener { documentSnapshot->
                if (documentSnapshot.exists()) {
                    val post = documentSnapshot.toObject(DiscoverPostModel::class.java)
                    if (post != null) {
                        _discoverPostComments.value = post!!.comments
                    }
                }
            }
            .addOnFailureListener { exception ->
                _message.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
    private fun sendComment(postId : String,myComment : CommentModel,notification : InAppNotificationModel) = viewModelScope.launch{
        val mutableList = mutableListOf<CommentModel>()
        mutableList.addAll(discoverPostComments.value ?: emptyList())
        mutableList.add(myComment)
        val likeData = hashMapOf<String,Any?>(
            "comments" to mutableList
        )
        firebaseRepo.commentToDiscoverPost(postId,likeData).addOnSuccessListener {
            getAllComments(postId)
            sendNotification(notification)
        }
    }
    fun createNotificationData(userToken : String,image : String) =
        InAppNotificationModel(
            userId = currentUserId,
            notificationType = NotificationType.POST,
            notificationId = UUID.randomUUID().toString(),
            title = "Gönderine Yorum yaptı",
            message = "${currentUserData.value?.fullName}: $message!",
            userImage = currentUserData.value?.profileImageUrl.toString(),
            imageUrl = image,
            userToken = userToken,
            time = getCurrentTime()
        )

    fun makeComment(postId : String,comment : String,notification : InAppNotificationModel){
        val commentId = UUID.randomUUID().toString()
        val myComment = CommentModel(
            commentId,comment,currentUserId,_userData.value?.profileImageUrl.toString(),
            userData.value?.username.toString(),getCurrentTime()
        )
        sendComment(postId,myComment,notification)
    }

    private fun getUserDataFromFirebase() {
        _message.value = Resource.loading(null)
        firebaseRepo.getUserDataByDocumentId(currentUserId)
            .addOnSuccessListener { documentSnapshot->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(UserModel::class.java)
                    if (user != null) {
                        _userData.value = user ?: UserModel()
                    }
                }
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _message.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
}