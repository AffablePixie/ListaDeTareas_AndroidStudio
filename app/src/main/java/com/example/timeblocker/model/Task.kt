package com.example.timeblocker.model

import java.time.LocalDateTime

data class Task(
    val id: Long,
    val title: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
    var isDone: Boolean = false,
    var completedInTime: Boolean = false
)
