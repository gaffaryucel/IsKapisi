package com.androiddevelopers.freelanceapp.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.model.VideoModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreateShortVideoViewModel  @Inject constructor(
    private val firebaseRepo : FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage,
) : ViewModel() {
    private var _insertVideoMessage = MutableLiveData<Resource<Boolean>>()
    val insertVideoMessage : LiveData<Resource<Boolean>>
        get() = _insertVideoMessage

    private fun saveVideoToStorage(uri : Uri,video : VideoModel){
        _insertVideoMessage.value = Resource.loading(null)
        val videoRef =  storage.reference
            .child("videos/"+ uri.lastPathSegment )
        videoRef.putFile(uri)
            .addOnSuccessListener {
                videoRef.downloadUrl.addOnSuccessListener {downloadUrl->
                    video.videoUrl = downloadUrl.toString()
                    postToFirestore(video)
                }
            }.addOnFailureListener{
                _insertVideoMessage.value = Resource.error(it.localizedMessage ?: "error",null)
            }
    }
    private fun postToFirestore(video : VideoModel){
        firebaseRepo.saveVideoToFirestore(video)
            .addOnSuccessListener {
                _insertVideoMessage.value = Resource.success(null)
            }.addOnFailureListener {
                _insertVideoMessage.value = Resource.error(it.localizedMessage ?: "error",null)
            }
    }

    fun createVideoModel(
        videoUrl: String?,
        title: String?,
        description: String?,
        likesCount: Int?,
        commentsCount: Int?,
        tags: List<String>?,
        uri : Uri,
    ) {
        val videoId = UUID.randomUUID().toString()
        val userId = firebaseAuth.currentUser?.uid
        val video = VideoModel(
            videoId = videoId,
            userId = userId,
            videoUrl = videoUrl,
            thumbnailUrl = "",
            title = title,
            description = description,
            likesCount = likesCount,
            commentsCount = commentsCount,
            timestamp = getCurrentTimeInMillis(),
            tags = tags
        )
        saveVideoToStorage(uri,video)
    }
    private fun getCurrentTimeInMillis(): Long {
        return System.currentTimeMillis()
    }

}