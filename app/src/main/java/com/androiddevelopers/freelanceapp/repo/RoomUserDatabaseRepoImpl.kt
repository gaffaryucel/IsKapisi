package com.androiddevelopers.freelanceapp.repo

import androidx.lifecycle.LiveData
import com.androiddevelopers.freelanceapp.dao.UserDao
import com.androiddevelopers.freelanceapp.model.UserProfileModel
import javax.inject.Inject

class RoomUserDatabaseRepoImpl @Inject constructor(
    private val dao: UserDao
) : RoomUserDatabaseRepoInterface {
    override fun observeUserData(): LiveData<UserProfileModel> {
        return dao.getUserInfo()
    }

    override fun updateUser(user: UserProfileModel) {
        dao.update(user)
    }

    override fun insertUser(user: UserProfileModel) {
        dao.insert(user)
    }

    override fun deleteUser(user: UserProfileModel) {
        dao.delete(user)
    }

    override fun updateUserName(userId: String, userName: String) {
        dao.updateUserName(userId, userName)
    }

    override fun updateUserImage(userId: String, userImage: String) {
        dao.updateUserImage(userId, userImage)
    }
}