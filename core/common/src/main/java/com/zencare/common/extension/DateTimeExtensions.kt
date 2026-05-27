package com.zencare.common.extension

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun String.toInstant(): Instant = Instant.parse(this)

fun Instant.toLocalDateTime(): LocalDateTime =
    LocalDateTime.ofInstant(this, ZoneId.systemDefault())

fun Instant.format(pattern: String = "yyyy-MM-dd HH:mm"): String =
    this.toLocalDateTime().format(DateTimeFormatter.ofPattern(pattern))

fun Long.toDurationString(): String {
    val minutes = this / 60
    val seconds = this % 60
    return if (minutes > 0) "${minutes}'${seconds}\"" else "${seconds}\""
}
