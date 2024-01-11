package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.androiddevelopers.freelanceapp.util.Util
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val repo  : FirebaseRepoInterFace
): ViewModel() {

    private var _messages = MutableLiveData<List<MessageModel>>()
    val messages : LiveData<List<MessageModel>>
        get() = _messages

    private var _messageStatus = MutableLiveData<Resource<Boolean>>()
    val messageStatus : LiveData<Resource<Boolean>>
        get() = _messageStatus


    fun sendMessage(chatId : String, message : MessageModel,chatMateId : String) {
        _messageStatus.value = Resource.loading(null)
        repo.sendMessageToRealtimeDatabase(chatId,message)
            .addOnSuccessListener {
                _messageStatus.value = Resource.success(null)
            }
            .addOnFailureListener { error ->
                _messageStatus.value = error.localizedMessage?.let { Resource.error(it,null) }
            }
        repo.addMessageInChatMatesRoom(chatMateId,chatId,message)
    }

    fun getMessages(chatId : String) {
        _messageStatus.value = Resource.loading(null)
        repo.getAllMessagesFromRealtimeDatabase(chatId).addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messageList = mutableListOf<MessageModel>()

                    for (messageSnapshot in snapshot.children) {
                        val message = messageSnapshot.getValue(MessageModel::class.java)
                        message?.let {
                            messageList.add(it)
                        }
                    }
                    _messages.value = messageList
                }

                override fun onCancelled(error: DatabaseError) {
                    _messageStatus.value =  Resource.error(error.message,null) }
            }
        )
    }
}