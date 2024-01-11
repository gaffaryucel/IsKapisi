package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.model.ChatModel
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel  @Inject constructor(
    private val repo  : FirebaseRepoInterFace
): ViewModel() {

    private var _chatRooms = MutableLiveData<List<ChatModel>>()
    val chatRooms : LiveData<List<ChatModel>>
        get() = _chatRooms

    private var _messageStatus = MutableLiveData<Resource<Boolean>>()
    val messageStatus : LiveData<Resource<Boolean>>
        get() = _messageStatus



    fun createChatRoom(chatMateId : String,chat : ChatModel) {
        _messageStatus.value = Resource.loading(null)
        repo.createChatRoomForOwner(chat)
            .addOnSuccessListener {
                _messageStatus.value = Resource.success(null)
            }
            .addOnFailureListener { error ->
                _messageStatus.value = error.localizedMessage?.let { Resource.error(it,null) }
            }
        repo.createChatRoomForChatMate(chatMateId,chat)
    }

    fun getChatRooms () {
        _messageStatus.value = Resource.loading(null)
        repo.getAllChatRooms().addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messageList = mutableListOf<ChatModel>()

                    for (messageSnapshot in snapshot.children) {
                        val message = messageSnapshot.getValue(ChatModel::class.java)
                        message?.let {
                            messageList.add(it)
                        }
                    }
                    _chatRooms.value = messageList
                }

                override fun onCancelled(error: DatabaseError) {
                    _messageStatus.value =  Resource.error(error.message,null) }
            }
        )
    }
}