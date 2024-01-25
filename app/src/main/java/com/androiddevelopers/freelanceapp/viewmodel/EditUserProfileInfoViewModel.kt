package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.UserProfileModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.repo.RoomUserDatabaseRepoInterface
import com.androiddevelopers.freelanceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class EditUserProfileInfoViewModel  @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace,
    private val firebaseAuth: FirebaseAuth,
    private val roomRepo: RoomUserDatabaseRepoInterface,
): ViewModel() {
    private val userId = firebaseAuth.currentUser!!.uid

    private var _message = MutableLiveData<Resource<UserModel>>()
    val message: LiveData<Resource<UserModel>>
        get() = _message

    private val _userData = MutableLiveData<UserModel>()
    val userData: LiveData<UserModel>
        get() = _userData


    init {
        getUserDataFromFirebase()
    }

    private fun getUserDataFromFirebase() {
        _message.value = Resource.loading(null)
        firebaseRepo.getUserDataByDocumentId(userId)
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val user = documentSnapshot.toObject(UserModel::class.java)
                    if (user != null) {
                        _userData.value = user ?: UserModel()
                        _message.value = Resource.success(null)
                    } else {
                        _message.value = Resource.error("Belirtilen belge bulunamadı", null)
                    }
                } else {
                    // Belge yoksa işlemleri buraya ekleyebilirsiniz
                    _message.value = Resource.error("kullanıcı kaydedilmemiş", null)
                }
            }
            .addOnFailureListener { exception ->
                // Hata durzumunda işlemleri buraya ekleyebilirsiniz
                _message.value = Resource.error("Belge alınamadı. Hata: $exception", null)
            }
    }

    fun updateUserInfo(user: UserModel) {
        val updateData = hashMapOf(
            "fullName" to user.fullName,
            "skills" to user.skills,
            "city" to user.location?.city,
            "country" to user.location?.country,
            "education" to user.education,
            "languages" to user.languages,
            "workExperience" to user.workExperience,
            "socialMediaLinks" to user.socialMediaLinks,
            "contactInformation" to user.contactInformation,
            "paymentMethods" to user.paymentMethods
        )
        firebaseRepo.updateUserInfo(userId,updateData).addOnSuccessListener {
            print("succes")
        }.addOnFailureListener{
            print("fail")
        }
    }
}