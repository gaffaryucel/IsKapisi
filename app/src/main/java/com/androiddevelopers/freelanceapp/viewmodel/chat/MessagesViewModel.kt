package com.androiddevelopers.freelanceapp.viewmodel.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val repo: FirebaseRepoInterFace,
    auth: FirebaseAuth
) : BaseChatViewModel(repo, auth) {


    private var _messages = MutableLiveData<List<MessageModel>>()
    val messages: LiveData<List<MessageModel>>
        get() = _messages


    private var _messageStatus = MutableLiveData<Resource<Boolean>>()
    val messageStatus: LiveData<Resource<Boolean>>
        get() = _messageStatus


    fun sendMessage(
        chatId: String,
        messageData: String,
        messageReceiver: String,
    ) {
        val time = getCurrentTime()

        val usersMessage = createMessageModelForCurrentUser(
            messageData ,
            currentUserId ?: "" ,
            messageReceiver,
            time
        )

        _messageStatus.value = Resource.loading(null)
        repo.sendMessageToRealtimeDatabase(currentUserId, chatId, usersMessage)
            .addOnSuccessListener {
                _messageStatus.value = Resource.success(null)
                changeLastMessage(chatId,messageData,time,messageReceiver)
                repo.changeReceiverSeenStatus(messageReceiver,chatId)
            }
            .addOnFailureListener { error ->
                _messageStatus.value = error.localizedMessage?.let { Resource.error(it, null) }
            }
        repo.addMessageInChatMatesRoom(messageReceiver, chatId, usersMessage)
    }

    private fun changeLastMessage(chatId : String,messageData: String, time: String,messageReceiver : String) = viewModelScope.launch{
        repo.changeLastMessage(currentUserId,chatId,messageData,time)
        repo.changeLastMessageInChatMatesRoom(messageReceiver,chatId,messageData,time)
    }

    private fun createMessageModelForCurrentUser(
        messageData: String,
        messageSender: String,
        messageReceiver: String,
        time : String
    ) : MessageModel {
        val messageId = UUID.randomUUID().toString()
        return MessageModel(
            messageId,
            messageData,
            messageSender,
            messageReceiver,
            time
        )
    }

    fun getMessages(chatId: String) {
        _messageStatus.value = Resource.loading(null)
        repo.getAllMessagesFromRealtimeDatabase(currentUserId, chatId).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messageList = mutableListOf<MessageModel>()

                    for (messageSnapshot in snapshot.children) {
                        val message = messageSnapshot.getValue(MessageModel::class.java)
                        message?.let {
                            messageList.add(it)
                        }
                    }
                    val sortedList = sortListByDate(messageList)
                    _messages.value = sortedList
                    repo.seeMessage(currentUserId,chatId)
                }

                override fun onCancelled(error: DatabaseError) {
                    _messageStatus.value = Resource.error(error.message, null)
                }
            }
        )
    }
}