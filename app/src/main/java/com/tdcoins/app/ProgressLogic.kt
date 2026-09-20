package com.tdcoins.app

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class StreakResult(val days: Int, val activeDate: String)

fun updateStreak(currentDays: Int, lastActiveDate: String, today: LocalDate): StreakResult {
    val previous = runCatching { LocalDate.parse(lastActiveDate) }.getOrNull()
    val nextDays = when {
        previous == null -> 1
        previous == today -> currentDays.coerceAtLeast(1)
        ChronoUnit.DAYS.between(previous, today) == 1L -> currentDays.coerceAtLeast(1) + 1
        else -> 1
    }
    return StreakResult(days = nextDays, activeDate = today.toString())
}