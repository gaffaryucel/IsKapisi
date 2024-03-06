package com.androiddevelopers.freelanceapp.viewmodel.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.toUserModel
import com.androiddevelopers.freelanceapp.viewmodel.BaseNotificationViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class BaseChatViewModel @Inject constructor(
    private val repo: FirebaseRepoInterFace,
    auth: FirebaseAuth
) : BaseNotificationViewModel(repo, auth) {

    private var _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData


    fun getUserData(userId: String) {
        repo.getUserDataByDocumentId(userId)
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    documentSnapshot.toUserModel()?.let { userModel ->
                        _userData.value = userModel
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
            }
    }

    fun sortListByDate(yourList: List<MessageModel>): List<MessageModel> {
        return yourList.sortedBy { it.timestamp }
    }
}