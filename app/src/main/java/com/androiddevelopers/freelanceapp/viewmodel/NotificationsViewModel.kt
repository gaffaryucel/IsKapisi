package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.notification.InAppNotificationModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.androiddevelopers.freelanceapp.util.toInAppNotificationModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val currentUserId = firebaseAuth.currentUser?.uid.toString()

    private var _message = MutableLiveData<Resource<UserModel>>()
    val message: LiveData<Resource<UserModel>>
        get() = _message

    private val _followNotifications = MutableLiveData<List<InAppNotificationModel>>()
    val followNotifications: LiveData<List<InAppNotificationModel>>
        get() = _followNotifications


    private val _jobPostNotifications = MutableLiveData<List<InAppNotificationModel>>()
    val jobPostNotifications: LiveData<List<InAppNotificationModel>>
        get() = _jobPostNotifications

    private val _postNotifications = MutableLiveData<List<InAppNotificationModel>>()
    val postNotifications: LiveData<List<InAppNotificationModel>>
        get() = _postNotifications


    init {
        getFollowNotifications(10)
        getAllJobPostNotifications(10)
        getPostNotifications(10)
    }

    fun getFollowNotifications(limit: Long) {
        _message.value = Resource.loading(null)
        firebaseRepo.getFollowNotifications(currentUserId, limit)
            .addOnSuccessListener {
                val notificationList = mutableListOf<InAppNotificationModel>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toInAppNotificationModel()?.let { post -> notificationList.add(post) }
                    _message.value = Resource.success(null)
                }
                _followNotifications.value = notificationList
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                println("hata : " + exception)
                _message.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }

    fun getAllJobPostNotifications(limit: Long) {
        _message.value = Resource.loading(null)
        firebaseRepo.getJobPostNotifications(currentUserId, limit)
            .addOnSuccessListener {
                val notificationList = mutableListOf<InAppNotificationModel>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toInAppNotificationModel()?.let { post -> notificationList.add(post) }
                    _message.value = Resource.success(null)
                }
                _jobPostNotifications.value = notificationList
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                println("hata : " + exception)
                _message.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }

    fun getPostNotifications(limit: Long) {
        _message.value = Resource.loading(null)
        firebaseRepo.getPostNotifications(currentUserId, limit)
            .addOnSuccessListener {
                val notificationList = mutableListOf<InAppNotificationModel>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toInAppNotificationModel()?.let { post -> notificationList.add(post) }
                    _message.value = Resource.success(null)
                }
                _postNotifications.value = notificationList
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                println("hata : " + exception)
                _message.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
}
