package com.androiddevelopers.freelanceapp.viewmodel.freelancer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.model.PreChatModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.model.notification.InAppNotificationModel
import com.androiddevelopers.freelanceapp.model.notification.PreMessageObject
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.NotificationTypeForActions
import com.androiddevelopers.freelanceapp.util.Resource
import com.androiddevelopers.freelanceapp.util.toFreelancerJobPost
import com.androiddevelopers.freelanceapp.util.toUserModel
import com.androiddevelopers.freelanceapp.viewmodel.BaseNotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DetailPostViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace, auth: FirebaseAuth
) : BaseNotificationViewModel(firebaseRepo, auth) {


    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private var _firebaseLiveData = MutableLiveData<FreelancerJobPost>()
    val firebaseLiveData: LiveData<FreelancerJobPost>
        get() = _firebaseLiveData

    private var _firebaseUserData = MutableLiveData<UserModel>()
    val firebaseUserData: LiveData<UserModel>
        get() = _firebaseUserData

    private var _preChatList = MutableLiveData<Resource<Boolean>>()
    val preChatList: LiveData<Resource<Boolean>>
        get() = _preChatList

    private var _preChatRoomAction = MutableLiveData<Resource<PreChatModel>>()
    val preChatRoomAction = _preChatRoomAction

    fun getFreelancerJobPostWithDocumentByIdFromFirestore(documentId: String) =
        viewModelScope.launch {
            _firebaseMessage.value = Resource.loading(true)

            firebaseRepo.getFreelancerJobPostWithDocumentByIdFromFirestore(documentId)
                .addOnSuccessListener { document ->
                    document.toFreelancerJobPost()?.let {
                        _firebaseLiveData.value = it
                    } ?: run {
                        _firebaseMessage.value =
                            Resource.error("İlan alınırken hata oluştu.", false)
                    }

                    _firebaseMessage.value = Resource.loading(false)
                    _firebaseMessage.value = Resource.success(true)

                }.addOnFailureListener {
                    _firebaseMessage.value = Resource.loading(false)

                    it.localizedMessage?.let { message ->
                        Resource.error(message, false)
                    }
                }
        }

    fun getUserDataByDocumentId(documentId: String) = viewModelScope.launch {
        _firebaseMessage.value = Resource.loading(true)

        firebaseRepo.getUserDataByDocumentId(documentId).addOnSuccessListener { document ->
            document.toUserModel()?.let {
                _firebaseUserData.value = it
            } ?: run {
                _firebaseMessage.value =
                    Resource.error("Bu hesapla eşleşen kullanıcı bulunamadı", null)
            }

            _firebaseMessage.value = Resource.loading(false)
            _firebaseMessage.value = Resource.success(true)

        }.addOnFailureListener {
            _firebaseMessage.value = Resource.loading(false)

            it.localizedMessage?.let { message ->
                Resource.error(message, false)
            }
        }
    }

    private fun createPreChatRoom(
        preChatModel: PreChatModel, notification: InAppNotificationModel, message: String
    ) {
        firebaseRepo.createPreChatRoom(
            preChatModel.receiver.toString(), preChatModel.sender.toString(), preChatModel
        ).addOnCompleteListener {
            if (it.isSuccessful) {
                sendNotification(
                    notification = notification,
                    type = NotificationTypeForActions.PRE_MESSAGE,
                    poreMessage = PreMessageObject(
                        userId = currentUserId,
                        postId = preChatModel.postId.toString(),
                        type = "frl"
                    )
                )
                firebaseRepo.saveNotification(notification)
                sendFirstMessage(
                    preChatModel.postId.toString(), message, preChatModel.receiver.toString()
                )
                _preChatRoomAction.value = Resource.success(preChatModel)
            } else {
                _preChatRoomAction.value =
                    Resource.error(it.exception?.localizedMessage.toString(), null)
            }
        }
    }

    fun createPreChatModel(
        type: String,
        postId: String,
        receiver: String,
        receiverName: String,
        receiverImage: String,
        notification: InAppNotificationModel,
        message: String,
    ) {
        val preChat = PreChatModel(
            postId, type, currentUserId, receiver, receiverName, receiverImage, ""
        )
        createPreChatRoom(preChat, notification, message)
    }

    fun setMessageValue(value: Boolean) {
        if (value) {
            _preChatRoomAction.value = Resource.error("", null)
        }
    }

    fun getCreatedPreChats(postId: String) {
        firebaseRepo.getAllPreChatRooms(currentUserId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
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

    private fun sendFirstMessage(
        chatId: String,
        messageData: String,
        messageReceiver: String,
    ) {
        val usersMessage = creatMessageModelForCurrentUser(
            messageData, currentUserId, messageReceiver
        )
        firebaseRepo.sendMessageToPreChatRoom(
            currentUserId, messageReceiver, chatId, usersMessage
        )
    }

    private fun creatMessageModelForCurrentUser(
        messageData: String, messageSender: String, messageReceiver: String
    ): MessageModel {
        val messageId = UUID.randomUUID().toString()
        return MessageModel(
            messageId, messageData, messageSender, messageReceiver, getCurrentTime()
        )
    }
}