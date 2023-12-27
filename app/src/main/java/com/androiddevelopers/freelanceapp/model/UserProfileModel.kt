package com.androiddevelopers.freelanceapp.model

import androidx.room.*

@Entity(tableName = "user")
data class UserProfileModel(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "user_name")
    val username: String,

    @ColumnInfo(name = "user_email")
    val email: String?
)
