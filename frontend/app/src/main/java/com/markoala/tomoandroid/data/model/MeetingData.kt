package com.markoala.tomoandroid.data.model


data class MeetingSummary(
    val title: String,
    val location: String?,
    val time: String?,
    val peopleCounts: Int = 1
)
