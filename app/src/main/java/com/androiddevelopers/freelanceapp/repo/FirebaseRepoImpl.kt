package com.androiddevelopers.freelanceapp.repo

import com.androiddevelopers.freelanceapp.model.ChatModel
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.VideoModel
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

class FirebaseRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    database: FirebaseDatabase,
) : FirebaseRepoInterFace {
    private val userCollection = firestore.collection("users")
    private val postCollection = firestore.collection("posts")
    private val videoCollection = firestore.collection("videos")
    private val messagesReference = database.getReference("users")
    private val currentUserId = auth.currentUser?.uid ?: ""

    override fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }
    override fun forgotPassword(email: String): Task<Void> {
        return auth.sendPasswordResetEmail(email)
    }
    override fun register(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }
    override fun addUserToFirestore(data: UserModel): Task<Void> {
        return userCollection.document(data.userId.toString()).set(data)
    }
    override fun deleteUserFromFirestore(documentId: String): Task<Void> {
        return userCollection.document(documentId).delete()
    }
    override fun getUserDataByDocumentId(documentId: String): Task<DocumentSnapshot> {
        return userCollection.document(documentId).get()
    }
    override fun addFreelancerJobPostToFirestore(post: FreelancerJobPost): Task<Void> {
        return postCollection.document(post.postId.toString()).set(post)
    }
    override fun getAllFreelancerJobPostFromFirestore(): Task<QuerySnapshot> {
        return postCollection.get()
    }
    override fun saveVideoToFirestore(video: VideoModel):Task<Void> {
        return videoCollection.document(video.videoId.toString()).set(video)
    }
    override fun getVideoFromFirestore(): Task<QuerySnapshot> {
        return videoCollection.get()
    }
    override fun sendMessageToRealtimeDatabase(chatId : String,message: MessageModel): Task<Void> {
        return messagesReference.child(currentUserId).child(chatId).child("messages").child(message.messageId.toString()).setValue(message)
    }
    override fun addMessageInChatMatesRoom(chatMateId : String,chatId : String,message: MessageModel): Task<Void> {
        return messagesReference.child(chatMateId).child(chatId).child("messages").child(message.messageId.toString()).setValue(message)
    }
    override fun getAllMessagesFromRealtimeDatabase(chatId : String): DatabaseReference {
        return messagesReference.child(currentUserId).child(chatId).child("messages")
    }
    override fun createChatRoomForOwner(chat : ChatModel): Task<Void> {
        return messagesReference.child(currentUserId).child(chat.chatId.toString()).setValue(chat)
    }
    override fun createChatRoomForChatMate(userId : String,chat : ChatModel): Task<Void> {
        return messagesReference.child(userId).child(chat.chatId.toString()).setValue(chat)
    }
    override fun getAllChatRooms() : DatabaseReference {
        return messagesReference.child(currentUserId)
    }
}