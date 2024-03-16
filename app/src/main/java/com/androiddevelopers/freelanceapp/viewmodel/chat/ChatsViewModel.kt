package com.androiddevelopers.freelanceapp.viewmodel.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.ChatModel
import com.androiddevelopers.freelanceapp.model.PreChatModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel  @Inject constructor(
    private val repo  : FirebaseRepoInterFace,
    private val auth  : FirebaseAuth
): ViewModel() {

    val currentUserId = auth.currentUser?.let { it.uid }

    //Chat Rooms
    private var _chatRooms = MutableLiveData<List<ChatModel>>()
    val chatRooms : LiveData<List<ChatModel>>
        get() = _chatRooms
    private var _preChats = MutableLiveData<List<PreChatModel>>()
    val preChats : LiveData<List<PreChatModel>>
        get() = _preChats



    //Search Results
    private var _chatSearchResult = MutableLiveData<List<ChatModel>>()
    val chatSearchResult : LiveData<List<ChatModel>>
        get() = _chatSearchResult
    private var _preChatSearchResult = MutableLiveData<List<PreChatModel>>()
    val preChatSearchResult : LiveData<List<PreChatModel>>
        get() = _preChatSearchResult



    //Status
    private var _messageStatus = MutableLiveData<Resource<Boolean>>()
    val messageStatus : LiveData<Resource<Boolean>>
        get() = _messageStatus

    private var _message = MutableLiveData<Resource<Boolean>>()
    val message : LiveData<Resource<Boolean>>
        get() = _message


    init {
        getChatRooms()
        getPreChats()
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
    fun searchChatByUsername(query: String) = viewModelScope.launch{
        val list = chatRooms.value
        _chatSearchResult.value = list?.filter { it.receiverUserName!!.contains(query, ignoreCase = true) }
    }



    private fun getPreChats () {
        _message.value = Resource.loading(null)
        repo.getAllPreChatRooms(currentUserId ?: "").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatList = mutableListOf<PreChatModel>()
                    for (messageSnapshot in snapshot.children) {
                        val preChat = messageSnapshot.getValue(PreChatModel::class.java)
                        preChat?.let {
                            chatList.add(it)
                        }
                    }
                    _preChats.value = chatList
                }

                override fun onCancelled(error: DatabaseError) {
                    _message.value =  Resource.error(error.message,null) }
            }
        )
    }
    fun searchPreChatByUsername(query: String) = viewModelScope.launch{
        val list = preChats.value
        _preChatSearchResult.value = list?.filter { it.receiverName!!.contains(query, ignoreCase = true) }
    }
}