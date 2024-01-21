package com.androiddevelopers.freelanceapp.repo

import android.net.Uri
import com.androiddevelopers.freelanceapp.model.ChatModel
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.VideoModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask

interface FirebaseRepoInterFace {
    fun login(email: String, password: String): Task<AuthResult>
    fun forgotPassword(email: String): Task<Void>
    fun register(email: String, password: String): Task<AuthResult>
    fun addUserToFirestore(data: UserModel) : Task<Void>
    fun deleteUserFromFirestore(documentId: String): Task<Void>
    fun getUserDataByDocumentId(documentId: String): Task<DocumentSnapshot>
    fun addFreelancerJobPostToFirestore(post: FreelancerJobPost): Task<Void>
    fun getAllFreelancerJobPostFromFirestore(): Task<QuerySnapshot>
    fun saveVideoToFirestore(video: VideoModel): Task<Void>
    fun getVideoFromFirestore(): Task<QuerySnapshot>
    fun sendMessageToRealtimeDatabase(
        userId: String,
        chatId: String,
        message: MessageModel
    ): Task<Void>

    fun addMessageInChatMatesRoom(
        chatMateId: String,
        chatId: String,
        message: MessageModel
    ): Task<Void>
    fun getAllMessagesFromRealtimeDatabase(currentUserId : String,chatId : String): DatabaseReference
    fun createChatRoomForOwner(currentUserId : String,chat : ChatModel): Task<Void>
    fun createChatRoomForChatMate(userId : String,chat : ChatModel): Task<Void>
    fun getAllChatRooms(currentUserId : String) : DatabaseReference
    fun getUsersFromFirestore() : Task<QuerySnapshot>
    fun addEmployerJobPostToFirestore(job: EmployerJobPost): Task<Void>
    fun getAllEmployerJobPostFromFirestore(): Task<QuerySnapshot>
    fun addImageToStorage(uri: Uri, file: String): UploadTask

    fun uploadDiscoverPostToFirestore(post: DiscoverPostModel): Task<Void>
    fun getAllDiscoverPostsFromFirestore(post: DiscoverPostModel): Task<QuerySnapshot>


}

