package com.example.timeblocker.data

import com.example.timeblocker.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object TaskRepository {
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    fun addTask(task: Task) { _tasks.value = _tasks.value + task }
    fun updateTask(updated: Task) { _tasks.value = _tasks.value.map { if (it.id == updated.id) updated else it } }
}
