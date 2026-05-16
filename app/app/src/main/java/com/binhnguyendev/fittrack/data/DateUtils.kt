package com.binhnguyendev.fittrack.data

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

/** Date helpers. "Normalized date" = epoch-millis at 00:00 UTC for that day. */
object DateUtils {
    fun startOfDayUtc(epochMillis: Long): Long =
        Instant.ofEpochMilli(epochMillis)
            .truncatedTo(ChronoUnit.DAYS)
            .toEpochMilli()

    fun todayUtc(): Long = startOfDayUtc(System.currentTimeMillis())

    fun utcMillis(date: LocalDate): Long =
        date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

    fun toLocalDate(utcMillis: Long): LocalDate =
        Instant.ofEpochMilli(utcMillis).atZone(ZoneOffset.UTC).toLocalDate()

    /** Whole days between two normalized dates (later - earlier). */
    fun daysBetween(fromUtc: Long, toUtc: Long): Long =
        ChronoUnit.DAYS.between(
            Instant.ofEpochMilli(fromUtc).atZone(ZoneOffset.UTC).toLocalDate(),
            Instant.ofEpochMilli(toUtc).atZone(ZoneOffset.UTC).toLocalDate(),
        )
}
