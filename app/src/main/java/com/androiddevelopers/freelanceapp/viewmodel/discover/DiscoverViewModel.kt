package com.androiddevelopers.freelanceapp.viewmodel.discover

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.repo.RoomUserDatabaseRepoInterface
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel  @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace
): ViewModel() {

    private var _message = MutableLiveData<Resource<UserModel>>()
    val message: LiveData<Resource<UserModel>>
        get() = _message

    private val _postData = MutableLiveData<List<DiscoverPostModel>>()
    val postData: LiveData<List<DiscoverPostModel>>
        get() = _postData

    init {
        getAllDiscoverPostsFromFirestore()
    }

    private fun getAllDiscoverPostsFromFirestore() {
        _message.value = Resource.loading(null)
        firebaseRepo.getAllDiscoverPostsFromFirestore()
            .addOnSuccessListener {
                val postList = mutableListOf<DiscoverPostModel>()
                for (document in it.documents) {
                    // Belgeden her bir videoyu çek
                    val post = document.toObject(DiscoverPostModel::class.java)
                    post?.let { postList.add(post) }
                    _message.value = Resource.success(null)
                }
                _postData.value = postList
            }.addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _message.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
}