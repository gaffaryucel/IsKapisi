package com.androiddevelopers.freelanceapp.repo

import androidx.lifecycle.LiveData
import com.androiddevelopers.freelanceapp.model.UserProfileModel

interface RoomUserDatabaseRepoInterface {
    fun observeUserData(): LiveData<UserProfileModel>
    fun updateUser(user: UserProfileModel)
    fun insertUser(user: UserProfileModel)
    fun deleteUser(user: UserProfileModel)
}