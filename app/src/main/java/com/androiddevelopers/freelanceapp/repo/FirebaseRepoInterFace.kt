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
    // Auth işlemleri
    fun login(email: String, password: String): Task<AuthResult>
    fun forgotPassword(email: String): Task<Void>
    fun register(email: String, password: String): Task<AuthResult>

    // Firestore User işlemleri
    fun addUserToFirestore(data: UserModel): Task<Void>
    fun deleteUserFromFirestore(documentId: String): Task<Void>
    fun getUserDataByDocumentId(documentId: String): Task<DocumentSnapshot>
    fun getUsersFromFirestore(): Task<QuerySnapshot>
    fun uploadDataInUserNode(userId: String, data: Any, type: String, dataId: String): Task<Void>

    // Firestore Freelancer Job Post işlemleri
    fun addFreelancerJobPostToFirestore(post: FreelancerJobPost): Task<Void>
    fun getAllFreelancerJobPostFromFirestore(): Task<QuerySnapshot>
    fun getFreelancerJobPostWithDocumentByIdFromFirestore(documentId: String): Task<DocumentSnapshot>

    // Firestore Employer Job Post işlemleri
    fun addEmployerJobPostToFirestore(job: EmployerJobPost): Task<Void>
    fun getAllEmployerJobPostFromFirestore(): Task<QuerySnapshot>
    fun getEmployerJobPostWithDocumentByIdFromFirestore(documentId: String): Task<DocumentSnapshot>

    // Firestore Discover Post işlemleri
    fun uploadDiscoverPostToFirestore(post: DiscoverPostModel): Task<Void>
    fun getAllDiscoverPostsFromFirestore(): Task<QuerySnapshot>

    // Realtime Database Chat işlemleri
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

    fun getAllMessagesFromRealtimeDatabase(currentUserId: String, chatId: String): DatabaseReference
    fun createChatRoomForOwner(currentUserId: String, chat: ChatModel): Task<Void>
    fun createChatRoomForChatMate(userId: String, chat: ChatModel): Task<Void>
    fun getAllChatRooms(currentUserId: String): DatabaseReference

    // Firebase Storage işlemleri
    fun addImageToStorageForJobPosting(
        uri: Uri,
        uId: String,
        postId: String,
        file: String
    ): UploadTask
    fun addDiscoverPostImage(
        uri: Uri,
        uId: String,
        postId: String,
        file: String
    ): UploadTask

    //User Profile Data İşlemleri
    fun getAllDiscoverPostsFromUser(userId : String): Task<QuerySnapshot>
    fun getAllEmployerJobPostsFromUser(userId : String): Task<QuerySnapshot>
    fun getAllFreelancerJobPostsFromUser(userId : String): Task<QuerySnapshot>
    fun follow(follower : String,followed : String): Task<Void>
    fun addFollower(follower : String,followed : String): Task<Void>
    fun updateUserData(userId: String, updateData:  HashMap<String, Any?>): Task<Void>
}

