package com.androiddevelopers.freelanceapp.viewmodel.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.model.ChatModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel  @Inject constructor(
    private val repo  : FirebaseRepoInterFace,
    private val auth  : FirebaseAuth
): ViewModel() {

    val currentUserId = auth.currentUser?.let { it.uid }

    private var _chatRooms = MutableLiveData<List<ChatModel>>()
    val chatRooms : LiveData<List<ChatModel>>
        get() = _chatRooms



    private var _messageStatus = MutableLiveData<Resource<Boolean>>()
    val messageStatus : LiveData<Resource<Boolean>>
        get() = _messageStatus


    init {
        getChatRooms()
    }
    private fun getChatRooms () {
        _messageStatus.value = Resource.loading(null)
        repo.getAllChatRooms(currentUserId ?: "").addValueEventListener(
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