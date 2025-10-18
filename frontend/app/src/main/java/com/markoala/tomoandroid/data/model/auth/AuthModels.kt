package com.markoala.tomoandroid.data.model.auth

data class SignupResponse(
    val success: Boolean,
    val message: String
)

data class FirebaseLoginResponse(
    val success: Boolean,
    val message: String,
    val data: AuthTokenBundle
)

data class AuthTokenBundle(
    val accessToken: String,
    val refreshToken: String
)
