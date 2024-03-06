package com.androiddevelopers.freelanceapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androiddevelopers.freelanceapp.model.DiscoverPostModel
import com.androiddevelopers.freelanceapp.model.FollowModel
import com.androiddevelopers.freelanceapp.model.UserModel
import com.androiddevelopers.freelanceapp.model.jobpost.EmployerJobPost
import com.androiddevelopers.freelanceapp.model.jobpost.FreelancerJobPost
import com.androiddevelopers.freelanceapp.model.notification.InAppNotificationModel
import com.androiddevelopers.freelanceapp.repo.FirebaseRepoInterFace
import com.androiddevelopers.freelanceapp.util.NotificationType
import com.androiddevelopers.freelanceapp.util.NotificationTypeForActions
import com.androiddevelopers.freelanceapp.util.Resource
import com.androiddevelopers.freelanceapp.util.toDiscoverPostModel
import com.androiddevelopers.freelanceapp.util.toEmployerJobPost
import com.androiddevelopers.freelanceapp.util.toFreelancerJobPost
import com.androiddevelopers.freelanceapp.util.toUserModel
import com.androiddevelopers.freelanceapp.viewmodel.profile.BaseProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val firebaseRepo: FirebaseRepoInterFace, auth: FirebaseAuth
) : BaseProfileViewModel(firebaseRepo, auth) {

    private var _userMessage = MutableLiveData<Resource<UserModel>>()
    val userMessage: LiveData<Resource<UserModel>>
        get() = _userMessage

    //User
    private val _userInfo = MutableLiveData<UserModel>()
    val userInfo: LiveData<UserModel>
        get() = _userInfo


    //Posts
    private val _freelancerJobPosts = MutableLiveData<List<FreelancerJobPost>>()
    val freelanceJobPosts: LiveData<List<FreelancerJobPost>>
        get() = _freelancerJobPosts

    private val _employerJobPosts = MutableLiveData<List<EmployerJobPost>>()
    val employerJobPosts: LiveData<List<EmployerJobPost>>
        get() = _employerJobPosts

    private val _discoverPosts = MutableLiveData<List<DiscoverPostModel>>()
    val discoverPosts: LiveData<List<DiscoverPostModel>>
        get() = _discoverPosts


    //Follow
    private val _followStatus = MutableLiveData<Resource<Boolean>>()
    val followStatus: LiveData<Resource<Boolean>>
        get() = _followStatus

    private var _followerCount = MutableLiveData<Long>()
    val followerCount = _followerCount

    fun getUserDataFromFirebase(userId: String) {
        firebaseRepo.getUserDataByDocumentId(userId).addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                documentSnapshot.toUserModel()?.let { userModel ->
                    _userInfo.value = userModel
                    _userMessage.value = Resource.success(null)
                    getDiscoverPostsFromUser(userId)
                    getEmployerJobPostsFromUser(userId)
                    getFreelancerJobPostsFromUser(userId)
                } ?: run {
                    _userMessage.value = Resource.error("Belirtilen belge bulunamadı", null)
                }
            } else {
                // Belge yoksa işlemleri buraya ekleyebilirsiniz
                _userMessage.value = Resource.error("kullanıcı kaydedilmemiş", null)
            }
        }.addOnFailureListener { exception ->
            // Hata durzumunda işlemleri buraya ekleyebilirsiniz
            _userMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
        }
    }

    private fun getFreelancerJobPostsFromUser(userId: String) {
        firebaseRepo.getAllFreelancerJobPostsFromUser(userId).addOnSuccessListener {
            val postList = mutableListOf<FreelancerJobPost>()
            for (document in it.documents) {
                // Belgeden her bir videoyu çek
                document.toFreelancerJobPost()?.let { post -> postList.add(post) }
            }
            _freelancerJobPosts.value = postList
        }.addOnFailureListener { exception ->
            // Hata durzumunda işlemleri buraya ekleyebilirsiniz
            _userMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
        }
    }

    private fun getEmployerJobPostsFromUser(userId: String) {
        firebaseRepo.getAllEmployerJobPostsFromUser(userId).addOnSuccessListener {
            val postList = mutableListOf<EmployerJobPost>()
            for (document in it.documents) {
                // Belgeden her bir videoyu çek
                document.toEmployerJobPost()?.let { post -> postList.add(post) }
            }
            _employerJobPosts.value = postList
        }.addOnFailureListener { exception ->
            // Hata durzumunda işlemleri buraya ekleyebilirsiniz
            _userMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
        }
    }

    private fun getDiscoverPostsFromUser(userId: String) {
        firebaseRepo.getAllDiscoverPostsFromUser(userId).addOnSuccessListener {
            val postList = mutableListOf<DiscoverPostModel>()
            for (document in it.documents) {
                // Belgeden her bir videoyu çek
                document.toDiscoverPostModel()?.let { post -> postList.add(post) }
            }
            _discoverPosts.value = postList
        }.addOnFailureListener { exception ->
            // Hata durzumunda işlemleri buraya ekleyebilirsiniz
            _userMessage.value = Resource.error("Belge alınamadı. Hata: $exception", null)
        }
    }

    private fun follow(followerModel: FollowModel, followingModel: FollowModel, token: String) {
        firebaseRepo.follow(followerModel, followingModel).addOnSuccessListener {
            _followStatus.value = Resource.success(true)
            val notification = InAppNotificationModel(
                userId = followingModel.userId,
                notificationType = NotificationType.FOLLOW,
                notificationId = UUID.randomUUID().toString(),
                title = "Yeni bir takipçin var!",
                message = "${followerModel.userName} seni takip etmeye başladı.",
                userImage = followerModel.userImage,
                imageUrl = null,
                userToken = token
            )
            sendNotification(
                notification = notification,
                type = NotificationTypeForActions.FOLLOW,
                followObject = currentUserId
            )
            firebaseRepo.saveNotification(notification)

        }.addOnFailureListener {
            it.localizedMessage?.let { msg ->
                _followStatus.value = Resource.error(msg, null)
            }
        }
    }

    private fun unFollow() {
        firebaseRepo.unFollow(currentUserId, userInfo.value?.userId.toString())
            .addOnSuccessListener {
                _followStatus.value = Resource.success(false)
            }.addOnFailureListener {
                it.localizedMessage?.let { msg ->
                    _followStatus.value = Resource.error(msg, null)
                }
            }
    }

    fun followOrUnFollow(isFollowing: Boolean) {
        var followingName = ""
        var followingImage = ""
        var followingId = ""
        var myToken = ""
        userInfo.value?.apply {
            followingId = userId.toString()
            followingName = username.toString()
            followingImage = profileImageUrl.toString()
            myToken = token.toString()
        }
        val followingModel = FollowModel(followingId, followingName, followingImage)
        val followerModel =
            FollowModel(currentUserId, userData.value?.username, userData.value?.profileImageUrl)
        if (isFollowing) {
            try {
                unFollow()
            } catch (e: Exception) {
                //
            }
        } else {
            try {
                follow(followerModel, followingModel, myToken)
            } catch (e: Exception) {
                //
            }
        }
    }

    fun getFollowers(userId: String) {
        firebaseRepo.getFollowers(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _followerCount.value = snapshot.childrenCount
                    for (user in snapshot.children) {
                        val key = user.key
                        if (key.equals(currentUserId)) {
                            _followStatus.value = Resource.success(true)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _followStatus.value = Resource.error(error.message, null)
                }

            })
    }
}