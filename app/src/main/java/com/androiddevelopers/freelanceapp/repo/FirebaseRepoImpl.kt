package com.androiddevelopers.freelanceapp.repo

import android.graphics.Bitmap
import android.net.Uri
import com.androiddevelopers.freelanceapp.model.ChatModel
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.FollowModel
import com.androiddevelopers.freelanceapp.model.MessageModel
import com.androiddevelopers.freelanceapp.model.PreChatModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.model.notification.InAppNotificationModel
import com.androiddevelopers.freelanceapp.model.notification.PushNotification
import com.androiddevelopers.freelanceapp.service.NotificationAPI
import com.androiddevelopers.freelanceapp.util.NotificationType
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.tasks.await
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

class FirebaseRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    firestore: FirebaseFirestore,
    database: FirebaseDatabase,
    storage: FirebaseStorage,
    private val notificationAPI: NotificationAPI
) : FirebaseRepoInterFace {
    //FirestoreRef
    private val userCollection = firestore.collection("users")
    private val notificationCollection = firestore.collection("notifications")
    private val freelancerPostCollection = firestore.collection("posts")
    private val employerPostCollection = firestore.collection("job_posting")
    private val discoverPostCollection = firestore.collection("discover_posts")

    //StorageRef
    private val imagesParentRef = storage.reference.child("users")
    private val profilePhotoRef = storage.reference

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

    override fun addEmployerPostToFirestore(job: EmployerJobPost): Task<Void> {
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
        savedUsers: List<String>
    ): Task<Void> {
        return employerPostCollection.document(postId).update("savedUsers", savedUsers)
    }

    override fun addFreelancerPostImage(
        uri: Uri,
        uId: String,
        postId: String,
    ): UploadTask {
        return imagesParentRef
            .child("userId=$uId")
            .child("images")
            .child("freelancerPost")
            .child("postId=$postId")
            .child("${UUID.randomUUID()}.jpg")
            .putFile(uri)
    }

    override fun addEmployerPostImage(
        uri: Uri,
        uId: String,
        postId: String,
    ): UploadTask {
        return imagesParentRef
            .child("userId=$uId")
            .child("images")
            .child("employerPost")
            .child("postId=$postId")
            .child("${UUID.randomUUID()}.jpg")
            .putFile(uri)
    }

    override fun addDiscoverPostImage(
        image: ByteArray,
        uId: String,
        postId: String
    ): UploadTask {
        return imagesParentRef
            .child("userId=$uId")
            .child("images")
            .child("discoverPost")
            .child("postId=$postId")
            .child("${UUID.randomUUID()}.jpg")
            .putBytes(image)
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

    override fun changeLastMessage(
        userId: String,
        chatId: String,
        message: String,
        time: String
    ): Task<Void> {
        val reference = messagesReference.child(userId).child(chatId)
        val updates = hashMapOf<String, Any>(
            "chatLastMessage" to message,
            "chatLastMessageTimestamp" to time
        )
        return reference.updateChildren(updates)
    }

    override fun changeLastMessageInChatMatesRoom(
        chatMateId: String,
        chatId: String,
        message: String,
        time: String
    ): Task<Void> {
        val reference = messagesReference.child(chatMateId).child(chatId)
        val updates = hashMapOf<String, Any>(
            "chatLastMessage" to message,
            "chatLastMessageTimestamp" to time
        )
        return reference.updateChildren(updates)
    }

    override fun seeMessage(userId: String, chatId: String): Task<Void> {
        val seen = hashMapOf<String, Any>(
            "seen" to true,
        )
        val userChatReference = messagesReference.child(userId).child(chatId)
        return userChatReference.updateChildren(seen)
    }
     override fun changeReceiverSeenStatus(receiver: String, chatId: String): Task<Void> {
        val unSeen = hashMapOf<String, Any>(
            "seen" to false,
        )
        val receiverChatReference = messagesReference.child(receiver).child(chatId)
        return receiverChatReference.updateChildren(unSeen)
    }



    override fun getAllFollowingUsers(currentUserId: String): DatabaseReference {
        return userFollowRef.child(currentUserId).child("following")
    }

    //PreChatRoom
    override fun getAllPreChatRooms(currentUserId: String): DatabaseReference {
        return preChatReference.child(currentUserId)
    }

    override fun createPreChatRoom(
        receiver: String,
        sender: String,
        chat: PreChatModel
    ): Task<Void> {
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
        receiver: String,
        chatId: String,
        message: MessageModel
    ): Task<Void> {
        preChatReference.child(receiver).child(chatId).child("messages")
            .child(message.messageId.toString()).setValue(message)
        return preChatReference.child(userId).child(chatId).child("messages")
            .child(message.messageId.toString()).setValue(message)
    }
    override fun changeLastPreMessage(
        userId: String,
        receiver: String,
        chatId: String,
        message: String,
        time: String
    ): Task<Void> {
        val updateUsersChatRoom = hashMapOf<String, Any>(
            "lastMessage" to message,
            "timestamp" to time
        )
        val updateReceiversChatRoom = hashMapOf<String, Any>(
            "lastMessage" to message,
            "timestamp" to time
        )
        val referenceUser = preChatReference.child(userId).child(chatId)
        val referenceReceiver = preChatReference.child(receiver).child(chatId)
        referenceReceiver.updateChildren(updateReceiversChatRoom)
        return referenceUser.updateChildren(updateUsersChatRoom)
    }

    override fun seePreMessage(userId: String, chatId: String): Task<Void> {
        val seen = hashMapOf<String, Any>(
            "seen" to true,
        )
        val userChatReference = preChatReference.child(userId).child(chatId)
        return userChatReference.updateChildren(seen)
    }

    override fun changeReceiverPreSeenStatus(receiverId: String, chatId: String): Task<Void> {
        val unSeen = hashMapOf<String, Any>(
            "seen" to false,
        )
        val receiverChatReference = preChatReference.child(receiverId).child(chatId)
        return receiverChatReference.updateChildren(unSeen)
    }


    //
    override fun getUsersFromFirestore(): Task<QuerySnapshot> {
        return userCollection.get()
    }

    override fun getUsersFromFirestore(list: List<String>): Task<QuerySnapshot> {
        return userCollection.whereIn(FieldPath.documentId(), list).get()
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


    override fun getAllDiscoverPostsFromUser(userId: String,limit : Long): Task<QuerySnapshot> {
        return discoverPostCollection.whereEqualTo("postOwner", userId).limit(limit).get()
    }

    override fun getAllEmployerJobPostsFromUser(userId: String,limit : Long): Task<QuerySnapshot> {
        return employerPostCollection.whereEqualTo("employerId", userId).limit(limit).get()
    }

    override fun getAllFreelancerJobPostsFromUser(userId: String,limit : Long): Task<QuerySnapshot> {
        return freelancerPostCollection.whereEqualTo("freelancerId", userId).limit(limit).get()
    }

    override fun follow(followerModel: FollowModel, followingModel: FollowModel): Task<Void> {
        //Karşı tarafın takipçilerine ekleme yapılacak kısım
        userFollowRef.child(followingModel.userId.toString()) // karşı tarafın Id'si altına iniyoruz
            .child("followers") //Onun takipçilerine ekleme yapmak için followers düğümü altına iniyoruz
            .child(followerModel.userId.toString()) //Biz takip ettiğimiz için kendi Id'miz ile bir anahtar oluşturuyoruz
            .setValue(followerModel) // Kendi bilgilerimizi onun takipçileri arasına verdik

        //Ben Takip Ettiğim için takipçilerime ekleme yapılacak kısım
        return userFollowRef.child(followerModel.userId.toString())//Benim Id altında
            .child("following") //Benim takip ettiklerimin olduğu kısım
            .child(followingModel.userId.toString()) //Benim takip ettiğim kişinin Id'sini kullanarak anhtar oluştur
            .setValue(followingModel) // Takip ettiğim kişinin bilgileri
    }

    override fun unFollow(currentUserId: String, followingId: String): Task<Void> {
        userFollowRef.child(followingId).child("followers").child(currentUserId).removeValue()
        return userFollowRef.child(currentUserId).child("following").child(followingId)
            .removeValue()
    }

    override fun getFollowers(userId: String): DatabaseReference {
        return userFollowRef.child(userId).child("followers")
    }

    override fun likePost(postId: String, updateData: HashMap<String, Any?>): Task<Void> {
        return discoverPostCollection.document(postId).update(updateData)
    }

    override fun getDiscoverPostDataFromFirebase(postId: String): Task<DocumentSnapshot> {
        return discoverPostCollection.document(postId).get()
    }

    override fun commentToDiscoverPost(
        postId: String,
        updateData: HashMap<String, Any?>
    ): Task<Void> {
        return discoverPostCollection.document(postId).update(updateData)
    }

    override suspend fun uploadPhotoToStorage(
        bitmap: Bitmap,
        uid: String,
        imagePath: String
    ): String? {
        val imagesRef = profilePhotoRef.child("$uid/$imagePath/${UUID.randomUUID()}.jpg")

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

    override fun updateUserData(userId: String, updateData: HashMap<String, Any?>): Task<Void> {
        return userCollection.document(userId).update(updateData)
    }

    //Notification
//Set
    override suspend fun postNotification(notification: PushNotification): Response<ResponseBody> {
        return notificationAPI.postNotification(notification)
    }

    override fun saveNotification(notification: InAppNotificationModel): Task<Void> {
        return notificationCollection.document(notification.notificationId.toString())
            .set(notification)
    }

 //Get
    override fun getNotificationsByType(userId: String,type : NotificationType ,limit: Long): Task<QuerySnapshot> {
        return notificationCollection.whereEqualTo("userId", userId)
            .whereEqualTo("notificationType", type)
            .limit(limit)
            .get()
    }
    override fun getAllNotifications(userId: String, limit: Long): Task<QuerySnapshot> {
        return notificationCollection.whereEqualTo("userId", userId)
            .limit(limit)
            .get()
    }

    override fun changeOnlineStatus(userId: String, onlineData: Boolean): Task<Void> {
        val map = hashMapOf<String, Any?>(
            "isOnline" to onlineData,
        )
        return userCollection.document(userId).update(map)
    }

}