package com.androiddevelopers.freelanceapp.repo

import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.VideoModel
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import javax.inject.Inject

class FirebaseRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    firestore: FirebaseFirestore,
) : FirebaseRepoInterFace {
    private val userCollection = firestore.collection("users")
    private val postCollection = firestore.collection("posts")
    private val videoCollection = firestore.collection("videos")

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
}