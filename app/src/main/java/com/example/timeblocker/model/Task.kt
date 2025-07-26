package com.example.timeblocker.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual
import java.time.LocalDateTime

@Serializable
data class Task(
    val id: Long,
    val title: String,
    @Contextual val start: LocalDateTime,
    @Contextual val end: LocalDateTime,
    val isDone: Boolean = false,
    val completedInTime: Boolean = false
)
