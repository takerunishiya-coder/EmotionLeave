package com.emotionleave.app.domain

import java.time.LocalDate
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Test

class StreakCalculatorTest {
    @Test
    fun homeStats_countsCurrentDayInclusively() {
        val stats = StreakCalculator.homeStats(
            startDate = LocalDate.of(2026, 5, 1),
            storedLongestStreakDays = 0,
            today = LocalDate.of(2026, 5, 7),
        )

        assertEquals(7, stats.currentDays)
        assertEquals(7, stats.longestDays)
        assertEquals(168, stats.cumulativeHours)
    }

    @Test
    fun homeStats_keepsStoredLongestWhenCurrentStreakIsShorter() {
        val stats = StreakCalculator.homeStats(
            startDate = LocalDate.of(2026, 5, 7),
            storedLongestStreakDays = 21,
            today = LocalDate.of(2026, 5, 7),
        )

        assertEquals(1, stats.currentDays)
        assertEquals(21, stats.longestDays)
        assertEquals(24, stats.cumulativeHours)
    }

    @Test
    fun relapseRestartStats_preservesFinishedStreakAsNewLongestAndIncrementsCount() {
        val stats = StreakCalculator.relapseRestartStats(
            previousStartDate = LocalDate.of(2026, 4, 30),
            storedLongestStreakDays = 3,
            storedRelapseCount = 2,
            today = LocalDate.of(2026, 5, 7),
        )

        assertEquals(8, stats.finishedStreakDays)
        assertEquals(8, stats.newLongestStreakDays)
        assertEquals(3, stats.newRelapseCount)
    }

    @Test
    fun daysSince_futureStartDoesNotReturnZeroOrNegative() {
        val days = StreakCalculator.daysSince(
            startDate = LocalDate.of(2026, 5, 8),
            today = LocalDate.of(2026, 5, 7),
        )

        assertEquals(1, days)
    }

    @Test
    fun epochMillisToLocalDate_usesProvidedZone() {
        val zone = ZoneId.of("Asia/Tokyo")
        val epochMillis = LocalDate.of(2026, 5, 20)
            .atStartOfDay(zone)
            .toInstant()
            .toEpochMilli()

        val date = StreakCalculator.epochMillisToLocalDate(
            value = epochMillis,
            zoneId = zone,
        )

        assertEquals(LocalDate.of(2026, 5, 20), date)
    }
}
