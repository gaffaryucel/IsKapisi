package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.repo.RoomUserDatabaseRepoInterface
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel  @Inject constructor(
    private val repo: FirebaseRepoInterFace,
    private val auth : FirebaseAuth,
): ViewModel() {
    private val userId = auth.currentUser!!.uid

    private var _userProfiles = MutableLiveData<List<UserModel>>()
    val userProfiles : LiveData<List<UserModel>>
        get() = _userProfiles

    private var _searchResult = MutableLiveData<List<UserModel>>()
    val searchResult : LiveData<List<UserModel>>
        get() = _searchResult

    private var _dataStatus = MutableLiveData<Resource<Boolean>>()
    val dataStatus : LiveData<Resource<Boolean>>
        get() = _dataStatus


    init {
        searchUser()
    }
    private fun searchUser(){
        _dataStatus.value = Resource.loading(null)
        repo.getUsersFromFirestore().addOnSuccessListener {
            _dataStatus.value = Resource.loading(false)
            it?.let { querySnapshot ->
                val list: ArrayList<UserModel> = ArrayList()
                querySnapshot.forEach { queryDocumentSnapshot ->
                    val user = queryDocumentSnapshot.toObject(UserModel::class.java)
                    if (user.userId != userId){
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
    fun searchByUsername(usernameToSearch: String) = viewModelScope.launch{
        delay(500)
        val list = userProfiles.value
        _searchResult.value = list?.filter { it.username!!.contains(usernameToSearch, ignoreCase = true) }
    }
}