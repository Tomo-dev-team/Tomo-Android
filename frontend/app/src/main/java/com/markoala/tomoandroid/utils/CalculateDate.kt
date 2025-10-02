package com.markoala.tomoandroid.utils

import java.time.LocalDate
import java.time.Period

fun calculateDate(friendSince: LocalDate): String {
    val today = LocalDate.now()
    val period = Period.between(friendSince, today)

    return when {
        period.years > 0 -> "${period.years}년째"
        period.months > 0 -> "${period.months}개월째"
        else -> "${period.days}일째"
    }
}
