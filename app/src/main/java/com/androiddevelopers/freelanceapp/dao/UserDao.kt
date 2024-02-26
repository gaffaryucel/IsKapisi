package com.androiddevelopers.freelanceapp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.androiddevelopers.freelanceapp.model.UserProfileModel

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: UserProfileModel)

    @Update
    fun update(user: UserProfileModel)

    @Delete
    fun delete(user: UserProfileModel)

    @Query("UPDATE user SET user_name = :userName WHERE user_id = :userId")
    fun updateUserName(userId: String, userName: String)

    @Query("UPDATE user SET user_photo = :userImage WHERE user_id = :userId")
    fun updateUserImage(userId: String, userImage: String)

    @Query("SELECT * FROM user")
    fun getUserInfo() : LiveData<UserProfileModel>

}