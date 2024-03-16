package com.androiddevelopers.freelanceapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.notification.InAppNotificationModel
import com.androiddevelopers.freelanceapp.model.notification.MessageObject
import com.androiddevelopers.freelanceapp.model.notification.NotificationData
import com.androiddevelopers.freelanceapp.model.notification.PreMessageObject
import com.androiddevelopers.freelanceapp.model.notification.PushNotification
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.NotificationTypeForActions
import com.androiddevelopers.freelanceapp.util.toUserModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
open class BaseNotificationViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    auth: FirebaseAuth
) : ViewModel() {

    val currentUserId = auth.currentUser?.uid.toString()

    private val _currentUserData = MutableLiveData<UserModel>()
    val currentUserData: LiveData<UserModel>
        get() = _currentUserData

    init {
        getCurrentUserData()
    }

    private fun getCurrentUserData() {
        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepo.getUserDataByDocumentId(currentUserId)
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        documentSnapshot.toUserModel()?.let { userModel ->
                            _currentUserData.value = userModel
                        }
                    }
                }
        }
    }

    internal fun sendNotification(
        notification: InAppNotificationModel,
        type: NotificationTypeForActions,
        poreMessage: PreMessageObject? = null,
        messageObject: MessageObject? = null,
        freelancerPostObject: String? = null,
        employerPostObject: String? = null,
        discoverPostObject: String? = null,
        like: String? = null,
        comment: String? = null,
        followObject: String? = null,
        receiverId : String? = null
    ) = CoroutineScope(Dispatchers.IO).launch {
        if (receiverId != currentUserId){
            val TAG = "Notification"
            try {
                PushNotification(
                    NotificationData(
                        title = notification.title.toString(),
                        message = notification.message.toString(),
                        imageUrl = notification.imageUrl.toString(),
                        profileImage = notification.userImage.toString(),
                        type = type,
                        preMessageObject = poreMessage,
                        messageObject = messageObject,
                        freelancerPostObject = freelancerPostObject,
                        employerPostObject = employerPostObject,
                        discoverPostObject = discoverPostObject,
                        like = like,
                        comment = comment,
                        followObject = followObject
                    ),
                    notification.userToken.toString()
                ).also {
                    firebaseRepo.postNotification(it)
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    internal fun getCurrentTime(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val date = Date(currentTime)
        return dateFormat.format(date)
    }
}