package com.androiddevelopers.freelanceapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.notification.InAppNotificationModel
import com.androiddevelopers.freelanceapp.model.notification.NotificationData
import com.androiddevelopers.freelanceapp.model.notification.PushNotification
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
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

    private fun getCurrentUserData(){
        viewModelScope.launch(Dispatchers.IO) {
            firebaseRepo.getUserDataByDocumentId(currentUserId)
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val user = documentSnapshot.toObject(UserModel::class.java)
                        if (user != null) {
                            _currentUserData.value = user ?: UserModel()
                        }
                    }
                }

        }
    }
    internal fun sendNotification(
        notification : InAppNotificationModel
    ) = CoroutineScope(Dispatchers.IO).launch {
        if (currentUserData.value?.userId.equals(notification.userId)){
            return@launch
        }
        val TAG = "Notification"
        try {
            PushNotification(
                NotificationData(notification.title.toString(),
                    notification.message.toString(),
                    notification.imageUrl.toString(),
                    notification.userImage.toString()),
                notification.userToken.toString()
            ).also {
                firebaseRepo.postNotification(it)
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }

    }
    internal fun getCurrentTime(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        val date = Date(currentTime)
        return dateFormat.format(date)
    }
}