package com.androiddevelopers.freelanceapp.viewmodel.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.androiddevelopers.freelanceapp.util.toEmployerJobPost
import com.androiddevelopers.freelanceapp.util.toFreelancerJobPost
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PreMessagingViewModel @Inject constructor(
    private val repo: FirebaseRepoInterFace,
    auth: FirebaseAuth
) : BaseChatViewModel(repo, auth) {

    //ön sohbet odasının oluşturulması ve kullanılması gerekli


    private var _messages = MutableLiveData<List<MessageModel>>()
    val messages: LiveData<List<MessageModel>>
        get() = _messages

    private var _messageStatus = MutableLiveData<Resource<Boolean>>()
    val messageStatus: LiveData<Resource<Boolean>>
        get() = _messageStatus

    private var _receiverMessage = MutableLiveData<Resource<UserModel>>()
    val receiverMessage: LiveData<Resource<UserModel>>
        get() = _receiverMessage

    private var _firebaseMessage = MutableLiveData<Resource<String>>()
    val firebaseMessage: LiveData<Resource<String>>
        get() = _firebaseMessage

    private var _freelancerPost = MutableLiveData<FreelancerJobPost>()
    val freelancerPost: LiveData<FreelancerJobPost>
        get() = _freelancerPost

    private var _employerPost = MutableLiveData<EmployerJobPost>()
    val employerPost: LiveData<EmployerJobPost>
        get() = _employerPost

    fun sendMessage(
        chatId: String,
        messageData: String,
        messageReceiver: String,
    ) {
        val usersMessage = createChatModelForCurrentUser(
            messageData,
            currentUserId,
            messageReceiver
        )

        _messageStatus.value = Resource.loading(null)
        repo.sendMessageToPreChatRoom(
            currentUserId,
            messageReceiver,
            chatId,
            usersMessage
        )
            .addOnSuccessListener {
                _messageStatus.value = Resource.success(null)
            }
            .addOnFailureListener { error ->
                _messageStatus.value = error.localizedMessage?.let { Resource.error(it, null) }
            }
    }

    private fun createChatModelForCurrentUser(
        messageData: String,
        messageSender: String,
        messageReceiver: String
    ): MessageModel {
        val messageId = UUID.randomUUID().toString()
        return MessageModel(
            messageId,
            messageData,
            messageSender,
            messageReceiver,
            getCurrentTime()
        )
    }

    fun getMessages(chatId: String) {
        _messageStatus.value = Resource.loading(null)
        repo.getAllMessagesFromPreChatRoom(currentUserId, chatId).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messageList = mutableListOf<MessageModel>()

                    for (messageSnapshot in snapshot.children) {
                        val message = messageSnapshot.getValue(MessageModel::class.java)
                        message?.let {
                            messageList.add(it)
                        }
                    }
                    _messageStatus.value = Resource.success(null)
                    val sortedList = sortListByDate(messageList)
                    _messages.value = sortedList
                }

                override fun onCancelled(error: DatabaseError) {
                    _messageStatus.value = Resource.error(error.message, null)
                }
            }
        )
    }

    fun getEmployerJobPostWithDocumentByIdFromFirestore(documentId: String) =
        viewModelScope.launch {
            _firebaseMessage.value = Resource.loading(null)

            repo.getEmployerJobPostWithDocumentByIdFromFirestore(documentId)
                .addOnSuccessListener { document ->
                    document.toEmployerJobPost()?.let { employerJobPost ->
                        _employerPost.value = employerJobPost
                    } ?: run {
                        _firebaseMessage.value =
                            Resource.error("İlan alınırken hata oluştu.", null)
                    }

                    _firebaseMessage.value = Resource.success(null)

                }.addOnFailureListener {
                    it.localizedMessage?.let { message ->
                        _firebaseMessage.value = Resource.error(message, null)
                    }
                }
        }

    fun getFreelancerJobPostWithDocumentByIdFromFirestore(documentId: String) =
        viewModelScope.launch {
            _firebaseMessage.value = Resource.loading(null)

            repo.getFreelancerJobPostWithDocumentByIdFromFirestore(documentId)
                .addOnSuccessListener { document ->
                    document.toFreelancerJobPost()?.let { freelancerJobPost ->
                        _freelancerPost.value = freelancerJobPost
                    } ?: run {
                        _firebaseMessage.value =
                            Resource.error("İlan alınırken hata oluştu.", null)
                    }
                    _firebaseMessage.value = Resource.success(null)

                }.addOnFailureListener {
                    it.localizedMessage?.let { message ->
                        _firebaseMessage.value = Resource.error(message, null)
                    }
                }
        }
}