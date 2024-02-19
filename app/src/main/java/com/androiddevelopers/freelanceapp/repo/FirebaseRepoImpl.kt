package com.androiddevelopers.freelanceapp.repo

import android.graphics.Bitmap
import android.net.Uri
import com.androiddevelopers.freelanceapp.model.ChatModel
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.model.PreChatModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

class FirebaseRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    database: FirebaseDatabase,
    storage: FirebaseStorage
) : FirebaseRepoInterFace {
    //FirestoreRef
    private val userCollection = firestore.collection("users")
    private val freelancerPostCollection = firestore.collection("posts")
    private val employerPostCollection = firestore.collection("job_posting")
    private val discoverPostCollection = firestore.collection("discover_posts")
    //StorageRef
    private val imagesParentRef = storage.reference.child("user_images")
    private val profilePhotoRef  = storage.reference
    
    //RealtimeRef
    private val messagesReference = database.getReference("users")
    private val preChatReference = database.getReference("preChat").child("users")
    private val userFollowRef = database.getReference("users_follow")


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
        return freelancerPostCollection.document(post.postId.toString()).set(post)
    }

    override fun getAllFreelancerJobPostFromFirestore(): Task<QuerySnapshot> {
        return freelancerPostCollection.get()
    }

    override fun getFreelancerJobPostWithDocumentByIdFromFirestore(documentId: String): Task<DocumentSnapshot> {
        return freelancerPostCollection.document(documentId).get()
    }

    override fun updateViewCountFreelancerJobPostWithDocumentById(
        postId: String,
        newCount: List<String>
    ): Task<Void> {
        return freelancerPostCollection.document(postId).update("viewCount", newCount)
    }

    override fun deleteFreelancerJobPostFromFirestore(postId: String): Task<Void> {
        return freelancerPostCollection.document(postId).delete()
    }

    override fun updateLikeFreelancerJobPostFromFirestore(
        postId: String,
        likes: List<String>
    ): Task<Void> {
        return freelancerPostCollection.document(postId).update("likes", likes)
    }

    override fun updateSavedUsersFreelancerJobPostFromFirestore(
        postId: String,
        savedUsers: List<String>
    ): Task<Void> {
        return freelancerPostCollection.document(postId).update("savedUsers", savedUsers)
    }

    override fun addEmployerJobPostToFirestore(job: EmployerJobPost): Task<Void> {
        return employerPostCollection.document(job.postId.toString()).set(job)
    }

    override fun getAllEmployerJobPostFromFirestore(): Task<QuerySnapshot> {
        return employerPostCollection.get()
    }

    override fun getEmployerJobPostWithDocumentByIdFromFirestore(documentId: String): Task<DocumentSnapshot> {
        return employerPostCollection.document(documentId).get()
    }

    override fun updateViewCountEmployerJobPostWithDocumentById(
        postId: String,
        newCount: List<String>
    ): Task<Void> {
        return employerPostCollection.document(postId).update("viewCount", newCount)
    }

    override fun deleteEmployerJobPostFromFirestore(postId: String): Task<Void> {
        return employerPostCollection.document(postId).delete()
    }

    override fun updateSavedUsersEmployerJobPostFromFirestore(
        postId: String,
        likes: List<String>
    ): Task<Void> {
        return employerPostCollection.document(postId).update("savedUsers", likes)
    }

    override fun addImageToStorageForJobPosting(
        uri: Uri,
        uId: String,
        postId: String,
        file: String
    ): UploadTask {
        return imagesParentRef
            .child(uId)
            .child("job_posts")
            .child(postId)
            .child(file)
            .putFile(uri)
    }

    override fun addDiscoverPostImage(
        uri: Uri,
        uId: String,
        postId: String,
        file: String
    ): UploadTask {
        return imagesParentRef
            .child(uId)
            .child("job_posts")
            .child(postId)
            .child(file)
            .putFile(uri)
    }

    override fun sendMessageToRealtimeDatabase(
        userId: String,
        chatId: String,
        message: MessageModel
    ): Task<Void> {
        return messagesReference.child(userId).child(chatId).child("messages")
            .child(message.messageId.toString()).setValue(message)
    }

    override fun addMessageInChatMatesRoom(
        chatMateId: String,
        chatId: String,
        message: MessageModel
    ): Task<Void> {
        return messagesReference.child(chatMateId).child(chatId).child("messages")
            .child(message.messageId.toString()).setValue(message)
    }

    override fun getAllMessagesFromRealtimeDatabase(
        currentUserId: String,
        chatId: String
    ): DatabaseReference {
        return messagesReference.child(currentUserId).child(chatId).child("messages")
    }

    override fun createChatRoomForOwner(currentUserId: String, chat: ChatModel): Task<Void> {
        return messagesReference.child(currentUserId).child(chat.chatId.toString()).setValue(chat)
    }

    override fun createChatRoomForChatMate(userId: String, chat: ChatModel): Task<Void> {
        return messagesReference.child(userId).child(chat.chatId.toString()).setValue(chat)
    }

    override fun getAllChatRooms(currentUserId: String): DatabaseReference {
        return messagesReference.child(currentUserId)
    }

//PreChatRoom
    override fun getAllPreChatRooms(currentUserId: String): DatabaseReference {
        return preChatReference.child(currentUserId)
    }
    override fun createPreChatRoom(receiver : String,sender: String, chat: PreChatModel): Task<Void> {
        preChatReference.child(receiver).child(chat.postId.toString()).setValue(chat)
        return preChatReference.child(sender).child(chat.postId.toString()).setValue(chat)
    }

//PreMessaging
    override fun getAllMessagesFromPreChatRoom(
        currentUserId: String,
        chatId: String
    ): DatabaseReference {
        return preChatReference.child(currentUserId).child(chatId).child("messages")
    }

    override fun sendMessageToPreChatRoom(
        userId: String,
        receiver : String,
        chatId: String,
        message: MessageModel
    ): Task<Void> {
        preChatReference.child(receiver).child(chatId).child("messages")
            .child(message.messageId.toString()).setValue(message)
        return preChatReference.child(userId).child(chatId).child("messages")
            .child(message.messageId.toString()).setValue(message)
    }
//
    override fun getUsersFromFirestore(): Task<QuerySnapshot> {
        return userCollection.get()
    }

    override fun uploadDiscoverPostToFirestore(post: DiscoverPostModel): Task<Void> {
        return discoverPostCollection.document(post.postId.toString()).set(post)
    }

    override fun getAllDiscoverPostsFromFirestore(): Task<QuerySnapshot> {
        return discoverPostCollection.get()
    }

    override fun uploadDataInUserNode(
        userId: String,
        data: Any,
        type: String,
        dataId: String
    ): Task<Void> {
        return userCollection.document(userId)
            .collection(type)
            .document(dataId)
            .set(data)
    }

   
   override fun getAllDiscoverPostsFromUser(userId : String): Task<QuerySnapshot> {
        return discoverPostCollection.whereEqualTo("postOwner", userId).get()
    }

    override fun getAllEmployerJobPostsFromUser(userId : String): Task<QuerySnapshot> {
        return employerPostCollection.whereEqualTo("employerId", userId).get()
    }

    override fun getAllFreelancerJobPostsFromUser(userId : String): Task<QuerySnapshot> {
        return freelancerPostCollection.whereEqualTo("freelancerId", userId).get()
    }

    override fun follow(currentUserId : String,followingId : String): Task<Void> {
        userFollowRef.child(followingId).child("followers").child(currentUserId).setValue(currentUserId)
        return userFollowRef.child(currentUserId).child("following").child(followingId).setValue(followingId)
    }
    override fun unFollow(currentUserId: String, followingId: String): Task<Void> {
        userFollowRef.child(followingId).child("followers").child(currentUserId).removeValue()
        return userFollowRef.child(currentUserId).child("following").child(followingId).removeValue()
    }
    override fun updateUserData(userId: String, updateData:  HashMap<String, Any?>): Task<Void> {
        return  userCollection.document(userId).update(updateData)
    }
    override fun getFollowers(userId: String): DatabaseReference {
        return userFollowRef.child(userId).child("followers")
    }
    override fun likePost(postId: String, updateData:  HashMap<String, Any?>): Task<Void> {
        return  discoverPostCollection.document(postId).update(updateData)
    }

    override fun getDiscoverPostDataFromFirebase(postId: String, ):Task<DocumentSnapshot> {
        return discoverPostCollection.document(postId).get()
    }
    override fun commentToDiscoverPost(postId: String, updateData:  HashMap<String, Any?>): Task<Void> {
        return  discoverPostCollection.document(postId).update(updateData)
    }
    override suspend fun uploadUserProfileImage(bitmap: Bitmap,uid : String): String? {
        val imagesRef = profilePhotoRef.child("$uid/profileImage/${UUID.randomUUID()}.jpg")

        return try {
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageData = baos.toByteArray()

            val uploadTask = imagesRef.putBytes(imageData)
            uploadTask.await() // Wait for the upload to finish

            if (uploadTask.isSuccessful) {
                val downloadUrl = imagesRef.downloadUrl.await()
                downloadUrl.toString()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }

    }

}