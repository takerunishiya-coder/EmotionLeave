package com.emotionleave.app.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

data class HomeStreakStats(
    val currentDays: Int,
    val longestDays: Int,
    val cumulativeHours: Int,
)

data class RelapseRestartStats(
    val finishedStreakDays: Int,
    val newLongestStreakDays: Int,
    val newRelapseCount: Int,
)

object StreakCalculator {
    fun daysSince(startDate: LocalDate?, today: LocalDate = LocalDate.now()): Int {
        if (startDate == null) return 1
        return (ChronoUnit.DAYS.between(startDate, today).coerceAtLeast(0) + 1).toInt()
    }

    fun homeStats(
        startDate: LocalDate?,
        storedLongestStreakDays: Int,
        today: LocalDate = LocalDate.now(),
    ): HomeStreakStats {
        val currentDays = daysSince(startDate, today)
        return HomeStreakStats(
            currentDays = currentDays,
            longestDays = maxOf(storedLongestStreakDays, currentDays),
            cumulativeHours = currentDays * 24,
        )
    }

    fun relapseRestartStats(
        previousStartDate: LocalDate?,
        storedLongestStreakDays: Int,
        storedRelapseCount: Int,
        today: LocalDate = LocalDate.now(),
    ): RelapseRestartStats {
        val finishedStreakDays = daysSince(previousStartDate, today)
        return RelapseRestartStats(
            finishedStreakDays = finishedStreakDays,
            newLongestStreakDays = maxOf(storedLongestStreakDays, finishedStreakDays),
            newRelapseCount = storedRelapseCount + 1,
        )
    }

    fun epochMillisToLocalDate(value: Long, zoneId: ZoneId = ZoneId.systemDefault()): LocalDate =
        Instant.ofEpochMilli(value)
            .atZone(zoneId)
            .toLocalDate()
}
