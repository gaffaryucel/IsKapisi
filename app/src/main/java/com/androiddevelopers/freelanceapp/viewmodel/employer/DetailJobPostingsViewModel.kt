package com.androiddevelopers.freelanceapp.viewmodel.employer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.model.PreChatModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
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
import javax.inject.Inject

@HiltViewModel
class DetailJobPostingsViewModel
@Inject
constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val currentUserId = auth.currentUser?.uid.toString()

    private var _firebaseMessage = MutableLiveData<Resource<Boolean>>()
    val firebaseMessage: LiveData<Resource<Boolean>>
        get() = _firebaseMessage

    private var _firebaseLiveData = MutableLiveData<EmployerJobPost>()
    val firebaseLiveData: LiveData<EmployerJobPost>
        get() = _firebaseLiveData

    private var _firebaseUserData = MutableLiveData<UserModel>()
    val firebaseUserData: LiveData<UserModel>
        get() = _firebaseUserData

    private var _preChatList = MutableLiveData<Resource<Boolean>>()
    val preChatList: LiveData<Resource<Boolean>>
        get() = _preChatList

    private var _preChatRoomAction = MutableLiveData<Resource<PreChatModel>>()
    val preChatRoomAction = _preChatRoomAction

    fun getEmployerJobPostWithDocumentByIdFromFirestore(documentId: String) =
        viewModelScope.launch {
            _firebaseMessage.value = Resource.loading(true)

            firebaseRepo.getEmployerJobPostWithDocumentByIdFromFirestore(documentId)
                .addOnSuccessListener { document ->
                    val employerJobPost = document.toObject(EmployerJobPost::class.java)

                    employerJobPost?.let {
                        _firebaseLiveData.value = it
                    } ?: run {
                        _firebaseMessage.value =
                            Resource.error("İlan alınırken hata oluştu.", false)
                    }

                    _firebaseMessage.value = Resource.loading(false)
                    _firebaseMessage.value = Resource.success(true)

                }.addOnFailureListener {
                    _firebaseMessage.value = Resource.loading(false)

                    it.localizedMessage?.let { message ->
                        _firebaseMessage.value = Resource.error(message, false)
                    }
                }
        }

    fun getUserDataByDocumentId(documentId: String) =
        viewModelScope.launch {
            _firebaseMessage.value = Resource.loading(true)

            firebaseRepo.getUserDataByDocumentId(documentId)
                .addOnSuccessListener { document ->
                    val userModel = document.toObject(UserModel::class.java)

                    userModel?.let {
                        _firebaseUserData.value = it
                    } ?: run {
                        _firebaseMessage.value =
                            Resource.error("Bu hesapla eşleşen kullanıcı bulunamadı", null)
                    }

                    _firebaseMessage.value = Resource.loading(false)
                    _firebaseMessage.value = Resource.success(true)

                }.addOnFailureListener {
                    _firebaseMessage.value = Resource.loading(false)

                    it.localizedMessage?.let { message ->
                        Resource.error(message, false)
                    }
                }
        }

    private fun createPreChatRoom(preChatModel: PreChatModel){
        firebaseRepo.createPreChatRoom(
            preChatModel.receiver.toString(),
            preChatModel.sender.toString(),
            preChatModel
        ).addOnCompleteListener{
            if (it.isSuccessful){
                _preChatRoomAction.value = Resource.success(preChatModel)
            }else{
                _preChatRoomAction.value = Resource.error(it.exception?.localizedMessage.toString(),null)
            }
        }
    }

    fun createPreChatModel(
        postId: String,
        receiver: String,
        receiverName: String,
        receiverImage: String,
    ){
        val preChat = PreChatModel(
            postId ,"emp",currentUserId, receiver, receiverName,
            receiverImage, "",getCurrentTime()
        )
        createPreChatRoom(preChat)
    }

    fun setMessageValue(value : Boolean){
        if (value){
            _preChatRoomAction.value = Resource.error("",null)
        }
    }

    fun getCreatedPreChats(postId: String){
        firebaseRepo.getAllPreChatRooms(currentUserId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (childSnapshot in snapshot.children) {
                    val key = childSnapshot.key
                    if (key.equals(postId)){
                        _preChatList.value = Resource.success(true)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _preChatList.value = Resource.error(error.toString(),true)
            }

        })
    }

    private fun getCurrentTime(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date(currentTime)
        return dateFormat.format(date)
    }
}