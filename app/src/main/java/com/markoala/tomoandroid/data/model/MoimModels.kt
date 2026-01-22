package com.markoala.tomoandroid.data.model

data class MoimListDTO(
    val moimId: Int,
    val title: String,
    val description: String,
    val emails: List<String>,
    val location: MoimLocationDTO,
    val leader: Boolean,
    val createdAt: String,
    val isPublic: Boolean
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

data class MoimDetails(
    val moimId: Int,
    val title: String,
    val description: String,
    val emails: List<String>,
    val location: MoimLocationDTO,
    val leader: Boolean,
    val createdAt: String,
    val isPublic: Boolean
)
