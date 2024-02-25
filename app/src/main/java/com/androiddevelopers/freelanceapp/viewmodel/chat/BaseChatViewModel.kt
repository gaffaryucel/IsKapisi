package com.androiddevelopers.freelanceapp.viewmodel.chat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.model.UserModel
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
open class BaseChatViewModel  @Inject constructor(
    private val repo  : FirebaseRepoInterFace,
    private val auth  : FirebaseAuth
): ViewModel() {
    val currentUserId = auth.currentUser?.let { it.uid }

    private val _currentUserData = MutableLiveData<UserModel>()
    val currentUserData : LiveData<UserModel>
        get() = _currentUserData

    private var _userData = MutableLiveData<UserModel>()
    val userData : LiveData<UserModel>
        get() = _userData

    init {
        getCurrentUserData()
    }
    fun getCurrentUserData(){
        repo.getUserDataByDocumentId(currentUserId.toString())
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(UserModel::class.java)
                    if (user != null) {
                        _currentUserData.value = user ?: UserModel()
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
            }
    }
    fun getUserData(userId : String){
        repo.getUserDataByDocumentId(userId)
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(UserModel::class.java)
                    if (user != null) {
                        _userData.value = user ?: UserModel()
                    }else{
                    }
                } else {
                    // Belge yoksa işlemleri buraya ekleyebilirsiniz
                }
            }
            .addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
            }
    }

    fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        val TAG = "Notification"
        try {
            repo.postNotification(notification)
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    fun getCurrentTime(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date(currentTime)
        return dateFormat.format(date)
    }
    fun sortListByDate(yourList: List<MessageModel>): List<MessageModel> {
        return yourList.sortedBy{it.timestamp}
    }
}