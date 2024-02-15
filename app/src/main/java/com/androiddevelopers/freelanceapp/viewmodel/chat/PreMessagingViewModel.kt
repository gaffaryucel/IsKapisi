package com.androiddevelopers.freelanceapp.viewmodel.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PreMessagingViewModel  @Inject constructor(
    private val repo  : FirebaseRepoInterFace,
    private val auth  : FirebaseAuth
): ViewModel() {

    //ön sohbet odasının oluşturulması ve kullanılması gerekli

    private val currentUserId = auth.currentUser?.let { it.uid }

    private var _messages = MutableLiveData<List<MessageModel>>()
    val messages: LiveData<List<MessageModel>>
        get() = _messages

    private var _messageStatus = MutableLiveData<Resource<Boolean>>()
    val messageStatus: LiveData<Resource<Boolean>>
        get() = _messageStatus

    private val _userData = MutableLiveData<UserModel>()
    val userData : LiveData<UserModel>
        get() = _userData

    private var _receiverMessage = MutableLiveData<Resource<UserModel>>()
    val receiverMessage : LiveData<Resource<UserModel>>
        get() = _receiverMessage

    private var _firebaseMessage = MutableLiveData<Resource<String>>()
    val firebaseMessage : LiveData<Resource<String>>
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
            currentUserId ?: "",
            messageReceiver
        )

        _messageStatus.value = Resource.loading(null)
        repo.sendMessageToPreChatRoom(currentUserId ?: "id yok",messageReceiver, chatId, usersMessage)
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
        repo.getAllMessagesFromPreChatRoom(currentUserId ?: "", chatId).addValueEventListener(
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

    private fun getCurrentTime(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date(currentTime)
        return dateFormat.format(date)
    }

    fun sortListByDate(yourList: List<MessageModel>): List<MessageModel> {
        return yourList.sortedBy { it.timestamp }
    }


    fun getUserDataFromFirebase(userId : String){
        repo.getUserDataByDocumentId(userId)
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(UserModel::class.java)
                    if (user != null) {
                        _userData.value = user ?: UserModel()
                        _receiverMessage.value = Resource.success(null)
                    }else{
                        _receiverMessage.value = Resource.error("Belirtilen belge bulunamadı",null)
                    }
                } else {
                    // Belge yoksa işlemleri buraya ekleyebilirsiniz
                    _receiverMessage.value = Resource.error("kullanıcı kaydedilmemiş",null)
                }
            }
            .addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _receiverMessage.value = Resource.error("Belge alınamadı. Hata: $exception",null)
            }
    }

    fun getEmployerJobPostWithDocumentByIdFromFirestore(documentId: String) =
        viewModelScope.launch {
            _firebaseMessage.value = Resource.loading(null)

            repo.getEmployerJobPostWithDocumentByIdFromFirestore(documentId)
                .addOnSuccessListener { document ->
                    val employerJobPost = document.toObject(EmployerJobPost::class.java)

                    employerJobPost?.let {
                        _employerPost.value = it
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
                    val freelancerJobPost = document.toObject(FreelancerJobPost::class.java)

                    freelancerJobPost?.let {
                        _freelancerPost.value = it

                    } ?: run {
                        _firebaseMessage.value =
                            Resource.error("İlan alınırken hata oluştu.", null)
                    }
                    _firebaseMessage.value = Resource.success(null)

                }.addOnFailureListener {
                    it.localizedMessage?.let { message ->
                        _firebaseMessage.value =  Resource.error(message, null)
                    }
                }
        }

}