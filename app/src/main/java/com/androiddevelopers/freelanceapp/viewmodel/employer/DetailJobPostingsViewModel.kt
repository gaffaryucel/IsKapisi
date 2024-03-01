package com.androiddevelopers.freelanceapp.viewmodel.employer

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androiddevelopers.freelanceapp.model.PreChatModel
import com.androiddevelopers.freelanceapp.model.notification.InAppNotificationModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DetailJobPostingsViewModel
@Inject
constructor(
    firebaseRepo: FirebaseRepoInterFace,
    sharedPreferences: SharedPreferences,
    auth: FirebaseAuth
) : BaseJobPostingViewModel(firebaseRepo,sharedPreferences,auth) {


    private var _preChatList = MutableLiveData<Resource<Boolean>>()
    val preChatList: LiveData<Resource<Boolean>>
        get() = _preChatList

    private var _preChatRoomAction = MutableLiveData<Resource<PreChatModel>>()
    val preChatRoomAction = _preChatRoomAction

    private fun createPreChatRoom(preChatModel: PreChatModel,notification : InAppNotificationModel){
        firebaseRepo.createPreChatRoom(
            preChatModel.receiver.toString(),
            preChatModel.sender.toString(),
            preChatModel
        ).addOnCompleteListener{
            if (it.isSuccessful){
                sendNotification(notification)
                firebaseRepo.saveNotification(notification)
                _preChatRoomAction.value = Resource.success(preChatModel)
            }else{
                _preChatRoomAction.value = Resource.error(it.exception?.localizedMessage.toString(),null)
            }
        }
    }

    fun createPreChatModel(
        type: String,
        postId: String,
        receiver: String,
        receiverName: String,
        receiverImage: String,
        notification : InAppNotificationModel,
    ){
        val preChat = PreChatModel(
            postId,
            type,
            currentUserId,
            receiver,
            receiverName,
            receiverImage,
            ""
        )
        createPreChatRoom(preChat,notification)
    }

    fun setMessageValue(value: Boolean) {
        if (value) {
            _preChatRoomAction.value = Resource.error("", null)
        }
    }

    fun getCreatedPreChats(postId: String) {
        firebaseRepo.getAllPreChatRooms(currentUserId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val key = childSnapshot.key
                    if (key.equals(postId)) {
                        _preChatList.value = Resource.success(true)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _preChatList.value = Resource.error(error.toString(), true)
            }

        })
    }




}