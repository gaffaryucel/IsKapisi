package com.androiddevelopers.freelanceapp.model

import androidx.room.*

@Entity(tableName = "user")
data class UserProfileModel(
    @ColumnInfo(name = "user_name")
    val username: String,
    @ColumnInfo(name = "user_photo")
    val userPhoto: String,
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "user_id")
    val userId: Int = 0
)
