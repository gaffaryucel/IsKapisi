package com.androiddevelopers.freelanceapp.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.notification.InAppNotificationModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.NotificationType
import com.androiddevelopers.freelanceapp.util.Resource
import com.androiddevelopers.freelanceapp.util.toInAppNotificationModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import java.time.LocalDate
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val currentUserId = firebaseAuth.currentUser?.uid.toString()

    private var _message = MutableLiveData<Resource<UserModel>>()
    val message: LiveData<Resource<UserModel>>
        get() = _message

    private val _notificationOfToday = MutableLiveData<List<InAppNotificationModel>>()
    val notificationOfToday: LiveData<List<InAppNotificationModel>>
        get() = _notificationOfToday

    private val _notificationOfLastWeek = MutableLiveData<List<InAppNotificationModel>>()
    val notificationOfLastWeek: LiveData<List<InAppNotificationModel>>
        get() = _notificationOfLastWeek

    private val _notificationOfEarlier = MutableLiveData<List<InAppNotificationModel>>()
    val notificationOfEarlier: LiveData<List<InAppNotificationModel>>
        get() = _notificationOfEarlier




    init {
        getAllNotifications(30)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun getAllNotifications(limit: Long) {
        _message.value = Resource.loading(null)
        firebaseRepo.getAllNotifications(currentUserId, limit)
            .addOnSuccessListener {
                val todayLst = mutableListOf<InAppNotificationModel>()
                val lastWeekList = mutableListOf<InAppNotificationModel>()
                val earlierList = mutableListOf<InAppNotificationModel>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toInAppNotificationModel()?.let { post ->
                        if (post.time != null){
                            if (isToday(post.time!!)){
                                todayLst.add(post)
                            }else if(isLastWeek(post.time!!)){
                                lastWeekList.add(post)
                            }else{
                                earlierList.add(post)
                            }
                        }
                    }
                    _message.value = Resource.success(null)
                }
                _notificationOfToday.value = sortListByDate(todayLst)
                _notificationOfLastWeek.value = sortListByDate(lastWeekList)
                _notificationOfEarlier.value = sortListByDate(earlierList)
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _message.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }


    fun getFollowNotifications(limit: Long) {
        _message.value = Resource.loading(null)
        firebaseRepo.getNotificationsByType(currentUserId,NotificationType.FOLLOW ,limit)
            .addOnSuccessListener {
                val notificationList = mutableListOf<InAppNotificationModel>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    document.toInAppNotificationModel()?.let { post -> notificationList.add(post) }
                    _message.value = Resource.success(null)
                }
                //_followNotifications.value = notificationList
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                println("hata : " + exception)
                _message.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }

    private fun isToday(date: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val tarih = LocalDateTime.parse(date, formatter)
        val bugun = LocalDate.now().atStartOfDay()

        return tarih.toLocalDate() == bugun.toLocalDate()
    }
    private fun isLastWeek(date: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")
        val tarih = LocalDateTime.parse(date, formatter)
        val bugun = LocalDate.now().atStartOfDay()

        return tarih.toLocalDate() >= bugun.minusDays(7).toLocalDate()
    }
    private fun sortListByDate(yourList: List<InAppNotificationModel>): List<InAppNotificationModel> {
        return yourList.sortedBy { it.time }
    }
}

