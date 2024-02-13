package com.androiddevelopers.freelanceapp.viewmodel.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.model.ChatModel
import com.androiddevelopers.freelanceapp.model.PreChatModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class PreChatViewModel @Inject constructor(
    private val repo  : FirebaseRepoInterFace,
    private val auth  : FirebaseAuth
): ViewModel() {

    val userId = auth.currentUser?.let { it.uid }

    private var _preChats = MutableLiveData<List<PreChatModel>>()
    val preChats : LiveData<List<PreChatModel>>
        get() = _preChats

    private var _userIdList = MutableLiveData<List<String>>()
    val userIdList : LiveData<List<String>>
        get() = _userIdList

    private var _message = MutableLiveData<Resource<Boolean>>()
    val message : LiveData<Resource<Boolean>>
        get() = _message


    init {
        getPreChats()
    }
    private fun getPreChats () {
        _message.value = Resource.loading(null)
        repo.getAllChatRooms(userId ?: "").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chatList = mutableListOf<PreChatModel>()
                    _message.value = Resource.success(null)
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
}