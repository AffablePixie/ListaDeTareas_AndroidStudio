package com.example.timeblocker.ui.list

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timeblocker.data.TaskRepository
import com.example.timeblocker.model.Task
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalDateTime

class TaskListScreenViewModel : ViewModel() {
    // Día seleccionado como StateFlow
    private val _selectedDay = MutableStateFlow(LocalDate.now())
    val selectedDay: StateFlow<LocalDate> = _selectedDay.asStateFlow()

    // Tareas expuestas desde el repositorio
    val tasks: StateFlow<List<Task>> = TaskRepository.tasks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    // Tareas filtradas por el día seleccionado
    val tasksForSelectedDay: StateFlow<List<Task>> = combine(TaskRepository.tasks, selectedDay) { list, day ->
        list.filter { it.start.toLocalDate() == day }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectDay(day: LocalDate) {
        _selectedDay.value = day
    }

    fun initialize(context: Context) {
        TaskRepository.initialize(context)
    }

    fun toggleDone(task: Task) {
       println("toggleDone llamado para id: ${task.id}")
       val now = LocalDateTime.now()
       val isDone = !task.isDone
       val completedInTime = isDone && now.isBefore(task.end)
       val updatedTask = task.copy(
           isDone = isDone,
           completedInTime = if (isDone) completedInTime else false
       )
       println("Llamando a updateTask con: $updatedTask")
       TaskRepository.updateTask(updatedTask)
    }

    fun removeTask(taskId: Long) {
        TaskRepository.removeTask(taskId)
    }

    fun addTask(task: Task) {
        TaskRepository.addTask(task)
    }

    fun updateTask(updatedTask: Task) {
        TaskRepository.updateTask(updatedTask)
    }
}
