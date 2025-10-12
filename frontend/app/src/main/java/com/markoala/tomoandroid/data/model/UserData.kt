package com.markoala.tomoandroid.data.model

data class UserData(
    val uuid: String,
    val email: String,
    val username: String
)

data class FriendProfile(
    val username: String,
    val email: String,
    val friendship: Double,
    val createdAt: String
)
