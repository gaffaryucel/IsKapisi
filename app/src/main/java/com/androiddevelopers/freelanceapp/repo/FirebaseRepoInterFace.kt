package com.androiddevelopers.freelanceapp.repo

import com.androiddevelopers.freelanceapp.model.ChatModel
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

interface FirebaseRepoInterFace {
    fun login(email: String, password: String): Task<AuthResult>
    fun forgotPassword(email: String): Task<Void>
    fun register(email: String, password: String): Task<AuthResult>
    fun addUserToFirestore(data: UserModel) : Task<Void>
    fun deleteUserFromFirestore(documentId: String): Task<Void>
    fun getUserDataByDocumentId(documentId: String): Task<DocumentSnapshot>
    fun addFreelancerJobPostToFirestore(post: FreelancerJobPost): Task<Void>
    fun getAllFreelancerJobPostFromFirestore(): Task<QuerySnapshot>
    fun saveVideoToFirestore(uri: VideoModel): Task<Void>
    fun getVideoFromFirestore():  Task<QuerySnapshot>
    fun sendMessageToRealtimeDatabase(userID : String,chatId : String,message: MessageModel): Task<Void>
    fun addMessageInChatMatesRoom(chatMateId : String,chatId : String,message: MessageModel): Task<Void>
    fun getAllMessagesFromRealtimeDatabase(currentUserId : String,chatId : String): DatabaseReference
    fun createChatRoomForOwner(currentUserId : String,chat : ChatModel): Task<Void>
    fun createChatRoomForChatMate(userId : String,chat : ChatModel): Task<Void>
    fun getAllChatRooms(currentUserId : String) : DatabaseReference
    fun getUsersFromFirestore() : Task<QuerySnapshot>
  
    fun addEmployerJobPostToFirestore(job: EmployerJobPost): Task<Void>
    fun getAllEmployerJobPostFromFirestore(): Task<QuerySnapshot>

}

