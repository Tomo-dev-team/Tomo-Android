package com.markoala.tomoandroid.data.model

data class MoimListDTO(
    val moimId: Int,
    val title: String,
    val description: String,
    val peopleCount: Int,
    val leader: Boolean,
    val createdAt: String
)

data class CreateMoimDTO(
    val title: String,
    val description: String,
    val isPublic: Boolean,
    val emails: List<String>,
    val location: MoimLocationDTO
)

data class MoimLocationDTO(
    val latitude: Double,
    val longitude: Double
)

data class Member(
    val email: String,
    val leader: Boolean
)

data class MoimDetails(
    val moimId: Int,
    val title: String,
    val description: String,
    val members: List<Member>,
    val createdAt: String
)
