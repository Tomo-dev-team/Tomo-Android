package com.markoala.tomoandroid.data.model

import java.time.LocalDate

data class UserData(
    val uuid: String,
    val email: String,
    val username: String
)

data class FriendProfile(
    val name: String,
    val email: String,
    val friendSince: LocalDate,
    val intimacy: Int
)
