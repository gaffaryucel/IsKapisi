package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.model.ChatModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private var _userIdList = MutableLiveData<List<String>>()
    val userIdList : LiveData<List<String>>
        get() = _userIdList
    init {
        getAllUsers()
        getExistedIds()
    }
    fun createChatRoom(userIdList : List<String>,userModel: UserModel) {
        _dataStatus.value = Resource.loading(null)
        if (roomIsExists(userIdList,userModel.userId.toString())){
            return
        }
        val chatId = UUID.randomUUID().toString()
        val chat = ChatModel(
            chatId,
            userModel.userId,
            userModel.username,
            userModel.profileImageUrl,
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

    private fun roomIsExists(users : List<String>,selectedId : String) : Boolean {
        return if (users.contains(selectedId)){
            _dataStatus.value = Resource.error("Bu sohbet odası zaten mevcut",null)
            true
        }else{
            false
        }
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

    private fun getAllUsers () {
        _dataStatus.value = Resource.loading(null)
        repo.getUsersFromFirestore().addOnSuccessListener {
            _dataStatus.value = Resource.loading(false)

            it?.let { querySnapshot ->
                val list: ArrayList<UserModel> = ArrayList()

                querySnapshot.forEach { queryDocumentSnapshot ->
                    val user = queryDocumentSnapshot.toObject(UserModel::class.java)
                    if (user.userId == currentUserId){
                        currentUserData.value = user
                    }else{
                        list.add(user)
                    }
                }
                _userProfiles.value = list
            }
        }.addOnFailureListener {
            _dataStatus.value = it.localizedMessage?.let { message ->
                Resource.error(message, false)
            }
        }
    }
    private fun getExistedIds() {
        repo.getAllChatRooms(currentUserId ?: "").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val keyList = mutableListOf<String>()


                    for (messageSnapshot in snapshot.children) {
                        // Diğer verileri almak için
                        // Diğer verileri almak için
                        val id = messageSnapshot.getValue(ChatModel::class.java)
                        id?.let {
                            keyList.add(it.receiverId.toString())
                        }
                    }
                    _userIdList.value = keyList
                }

                override fun onCancelled(error: DatabaseError) {
                    _dataStatus.value = Resource.error(error.message, null)
                }
            }
        )
    }


}