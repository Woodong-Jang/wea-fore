package com.example.weatherol.utils

import com.example.weatherol.data.common.AppConstants
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object TimeFormatUtils {

    fun formatEpochSeconds(
        epochSeconds: Long,
        pattern: String = AppConstants.TimePattern.DATE_TIME,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): String {
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSeconds), zoneId).format(formatter)
    }

    fun formatIsoDateTime(
        isoDateTime: String,
        targetPattern: String = AppConstants.TimePattern.HOUR_MINUTE
    ): String {
        val parsed = LocalDateTime.parse(isoDateTime)
        return parsed.format(DateTimeFormatter.ofPattern(targetPattern))
    }
}
