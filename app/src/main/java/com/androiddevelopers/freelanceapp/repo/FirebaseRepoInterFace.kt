package com.androiddevelopers.freelanceapp.repo

import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
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
}