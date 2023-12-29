package com.androiddevelopers.freelanceapp.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.androiddevelopers.freelanceapp.model.UserProfileModel

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(student: UserProfileModel)

    @Update
    fun update(user: UserProfileModel)

    @Delete
    fun delete(user: UserProfileModel)

    @Query("SELECT * FROM user")
    fun getUserInfo() : LiveData<UserProfileModel>

}