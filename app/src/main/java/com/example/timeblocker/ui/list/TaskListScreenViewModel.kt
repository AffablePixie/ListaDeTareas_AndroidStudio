package com.example.timeblocker.ui.list

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.timeblocker.data.TaskRepository
import com.example.timeblocker.model.Task
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

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

    /**
     * Calcula la hora de inicio por defecto para una nueva tarea en el día especificado.
     * Si no hay tareas en el día, devuelve 9:00.
     * Si hay tareas, devuelve la hora de fin de la última tarea que termine ese mismo día.
     */
    fun getDefaultStartTimeForDay(day: LocalDate): LocalTime {
        val tasksForDay = tasks.value.filter { task ->
            // Solo considerar tareas que empiecen en el día especificado
            task.start.toLocalDate() == day
        }
        
        if (tasksForDay.isEmpty()) {
            // Si no hay tareas, usar 9:00 por defecto
            return LocalTime.of(9, 0)
        }
        
        // Filtrar tareas que terminan en el mismo día (excluir las que terminan al día siguiente)
        val tasksEndingSameDay = tasksForDay.filter { task ->
            task.end.toLocalDate() == day
        }
        
        if (tasksEndingSameDay.isEmpty()) {
            // Si todas las tareas terminan al día siguiente, usar 9:00 por defecto
            return LocalTime.of(9, 0)
        }
        
        // Encontrar la hora de fin más tardía de las tareas que terminan ese día
        val latestEndTime = tasksEndingSameDay.maxOf { it.end.toLocalTime() }
        
        return latestEndTime
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
