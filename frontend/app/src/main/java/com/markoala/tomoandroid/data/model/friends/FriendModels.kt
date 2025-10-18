package com.markoala.tomoandroid.data.model.friends

data class FriendProfile(
    val username: String,
    val email: String,
    val friendship: Double,
    val createdAt: String
)

data class FriendSummary(
    val username: String,
    val email: String
)

data class FriendLookupResponse(
    val success: Boolean,
    val message: String,
    val data: FriendSummary
)

data class FriendListResponse(
    val success: Boolean,
    val message: String,
    val data: List<FriendProfile>
)

data class FriendSearchRequest(
    val email: String
)

data class FriendSearchResponse(
    val success: Boolean,
    val message: String,
    val data: FriendSummary?
)
