package com.androiddevelopers.freelanceapp.viewmodel.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.model.ChatModel
import com.androiddevelopers.freelanceapp.model.FollowModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateChatRoomViewModel  @Inject constructor(
    private val repo  : FirebaseRepoInterFace,
    private val auth  : FirebaseAuth
): ViewModel() {

    val currentUserId = auth.currentUser?.let { it.uid }

    private var _userProfiles = MutableLiveData<List<UserModel>>()
    val userProfiles : LiveData<List<UserModel>>
        get() = _userProfiles

    private var _dataStatus = MutableLiveData<Resource<Boolean>>()
    val dataStatus : LiveData<Resource<Boolean>>
        get() = _dataStatus

    private var currentUserData = MutableLiveData<UserModel>()

    private var _followingUsers = MutableLiveData<List<FollowModel>>()
    val followingUsers : LiveData<List<FollowModel>>
        get() = _followingUsers

    init {
        getExistedIds()
    }
    fun createChatRoom(user: FollowModel) {
        _dataStatus.value = Resource.loading(null)
        val chatId = UUID.randomUUID().toString()
        val chat = ChatModel(
            chatId,
            user.userId,
            user.userName,
            user.userImage,
            "",
            ""
        )
        repo.createChatRoomForOwner(currentUserId ?: "",chat)
            .addOnSuccessListener {
                _dataStatus.value = Resource.success(null)
            }
            .addOnFailureListener { error ->
                _dataStatus.value = error.localizedMessage?.let { Resource.error(it,null) }
            }
        val newChat = createChatForChatMate(chatId)
        repo.createChatRoomForChatMate(chat.receiverId.toString(),newChat)
    }
    private fun createChatForChatMate(chatId: String) : ChatModel {
        return ChatModel(
            chatId,
            currentUserId,
            currentUserData.value?.username,
            currentUserData.value?.profileImageUrl,
            "",
            ""
        )
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getAllUsers(created : List<String>) {
        _dataStatus.value = Resource.loading(null)
        GlobalScope.launch(Dispatchers.IO) {
            repo.getAllFollowingUsers(currentUserId.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val users = ArrayList<FollowModel>()
                        for (messageSnapshot in snapshot.children) {
                            val user = messageSnapshot.getValue(FollowModel::class.java)
                            if (user!= null){
                                if (user.userId != currentUserId){
                                    users.add(user)
                                }
                            }
                        }
                        val filteredList = users.filter { model ->
                            created.none { it == model.userId }
                        }
                        _followingUsers.value = filteredList
                    }

                    override fun onCancelled(error: DatabaseError) {
                        _dataStatus.value = Resource.error(error.message, null)
                    }
                })
        }

    }
    private fun getExistedIds() {
        repo.getAllChatRooms(currentUserId ?: "").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val keyList = mutableListOf<String>()
                    for (messageSnapshot in snapshot.children) {
                        val id = messageSnapshot.getValue(ChatModel::class.java)
                        id?.let {
                            keyList.add(it.receiverId.toString())
                        }
                        println("id : "+id)
                    }
                    getAllUsers(keyList)
                }

                override fun onCancelled(error: DatabaseError) {
                    _dataStatus.value = Resource.error(error.message, null)
                }
            }
        )
    }


}