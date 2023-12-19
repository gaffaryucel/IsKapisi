package com.androiddevelopers.freelanceapp.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class FirebaseRepoImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : FirebaseRepoInterFace {
    /*
    override fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email,password)
    }
    override fun register(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email,password)
    }
    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    override fun signOut() {
        auth.signOut()
    }
    override fun addUserToFirestore(data: UserProfile) {
        firestore.collection("users").document(data.userId.toString()).set(data)
    }
    override fun deleteUserFromFirestore(documentId: String) {
        firestore.collection("users").document(documentId).delete()
    }
    override fun getUserDataByDocumentId(documentId: String): Task<DocumentSnapshot> {
        return firestore.collection("users").document(documentId).get()
    }
     */
}