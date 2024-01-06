package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.VideoModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.repo.RoomUserDatabaseRepoInterface
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShortVideoViewModel  @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth,
    private val roomRepo: RoomUserDatabaseRepoInterface,
): ViewModel() {
    private val userId = firebaseAuth.currentUser!!.uid

    private var _message = MutableLiveData<Resource<UserModel>>()
    val message: LiveData<Resource<UserModel>>
        get() = _message

    private val _videoData = MutableLiveData<List<VideoModel>>()
    val videoData: LiveData<List<VideoModel>>
        get() = _videoData

    init {
        getUserDataFromFirebase()
    }

    private fun getUserDataFromFirebase() {
        _message.value = Resource.loading(null)
        firebaseRepo.getVideoFromFirestore()
            .addOnSuccessListener {
                val videos = mutableListOf<VideoModel>()
                for (document in it .documents) {
                    // Belgeden her bir videoyu çek
                    val video = document.toObject(VideoModel::class.java)
                    video?.let { videos.add(it) }
                }
                _videoData.value = videos
            }
            .addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                println("Belge alınamadı. Hata: $exception")
                _message.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }
}