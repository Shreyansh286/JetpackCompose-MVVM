package com.example.myjetpack1.model

data class UserResponse(
    val results: List<NetworkUser>,
    val info: Info
)

data class Info(
    val seed: String,
    val results: Int,
    val page: Int,
    val version: String

)
