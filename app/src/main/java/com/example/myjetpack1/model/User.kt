package com.example.myjetpack1.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val uuid: String, // Extracted from Login for simplicity as Primary Key
    @Embedded
    val name: Name,
    val email: String,
    @Embedded
    val picture: Picture
)

// Login class might not be directly needed in the User entity if uuid is extracted,
// but kept here if it's used elsewhere or if you prefer to embed it.
// If you embed Login, then uuid inside Login should be the PrimaryKey,
// and User would have: @Embedded val login: Login
data class Login(
    val uuid: String
)

data class Name(
    val first: String,
    val last: String
)

data class Picture(
    val large: String,
    val medium: String,
    val thumbnail: String
)
