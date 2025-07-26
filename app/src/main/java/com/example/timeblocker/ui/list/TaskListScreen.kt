package com.example.timeblocker.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.timeblocker.model.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import java.time.LocalDateTime
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import android.app.TimePickerDialog
import java.time.LocalTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.filled.CalendarToday

@Composable
fun TaskListScreen(
    viewModel: TaskListScreenViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.initialize(context)
    }
    val fmt = DateTimeFormatter.ofPattern("HH:mm")
    val selectedDay = viewModel.selectedDay.collectAsState().value
    val tasks = viewModel.tasksForSelectedDay.collectAsState().value

    // Estado para la semana base (lunes de la semana mostrada)
    var weekStart by remember { mutableStateOf(selectedDay.with(java.time.DayOfWeek.MONDAY)) }
    // Si el usuario selecciona un día fuera de la semana actual, actualiza la semana base
    LaunchedEffect(selectedDay) {
        if (selectedDay < weekStart || selectedDay > weekStart.plusDays(6)) {
            weekStart = selectedDay.with(java.time.DayOfWeek.MONDAY)
        }
    }
    val weekDays = (0..6).map { weekStart.plusDays(it.toLong()) }

    // Obtener meses de la semana
    val mesesSemana = weekDays.map { it.month.getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale("es")) }.distinct()
    val mesesTexto = if (mesesSemana.size == 1) mesesSemana[0].replaceFirstChar { it.uppercase() } else mesesSemana.joinToString(" - ") { it.replaceFirstChar { c -> c.uppercase() } }

    // Obtener años de la semana
    val aniosSemana = weekDays.map { it.year }.distinct()
    val aniosTexto = if (aniosSemana.size == 1) aniosSemana[0].toString() else aniosSemana.joinToString(" - ")
    val mesesYAnios = "$mesesTexto $aniosTexto"

    var showDialog by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        val currentEditingTask = editingTask
        Column {
            // Barra de meses, años, flechas y acceso rápido a hoy con centro perfectamente centrado y derecha visible
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Flecha izquierda
                IconButton(
                    onClick = { weekStart = weekStart.minusDays(7) },
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Semana anterior")
                }
                // Centro: mes y año perfectamente centrado
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mesesYAnios,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                // Derecha: flecha derecha y botón Hoy juntos
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { weekStart = weekStart.plusDays(7) },
                    ) {
                        Icon(Icons.Filled.ArrowForward, contentDescription = "Semana siguiente")
                    }
                    Button(
                        onClick = {
                            weekStart = LocalDate.now().with(java.time.DayOfWeek.MONDAY)
                            viewModel.selectDay(LocalDate.now())
                        },
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Icon(Icons.Filled.CalendarToday, contentDescription = "Ir a hoy", modifier = Modifier.padding(end = 4.dp))
                        Text("Hoy")
                    }
                }
            }
            // Barra de días de la semana
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                weekDays.forEach { day ->
                    val isSelected = day == selectedDay
                    val isToday = day == LocalDate.now()
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                            .clickable { viewModel.selectDay(day) },
                        shape = MaterialTheme.shapes.medium,
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 6.dp else 1.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isSelected -> MaterialTheme.colorScheme.primary
                                isToday && !isSelected -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.surface // Restaurar color de fondo del tema
                            },
                            contentColor = when {
                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                isToday && !isSelected -> MaterialTheme.colorScheme.onSecondaryContainer
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = day.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale("es")).replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = day.dayOfMonth.toString(),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
            // Lista de tareas filtradas
            LazyColumn {
                items(tasks, key = { it.id to it.isDone }) { task ->
                    TaskCard(
                        task = task,
                        onToggle = { viewModel.toggleDone(task) },
                        formatter = fmt,
                        onLongPress = { editingTask = task; showDialog = true }
                    )
                }
            }
        }

        // FAB para añadir tarea
        FloatingActionButton(
            onClick = { showDialog = true; editingTask = null },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Añadir tarea")
        }

        // Diálogo para nueva tarea o edición
        if (showDialog) {
            var title by remember { mutableStateOf(currentEditingTask?.title ?: "") }
            var startTime by remember { mutableStateOf(currentEditingTask?.start?.toLocalTime() ?: LocalTime.of(9, 0)) }
            var endTime by remember { mutableStateOf(currentEditingTask?.end?.toLocalTime() ?: LocalTime.of(10, 0)) }
            var error by remember { mutableStateOf("") }
            val context = LocalContext.current

            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (currentEditingTask == null) "Nueva tarea" else "Editar tarea") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Título") }
                        )
                        // Selector de hora de inicio
                        Button(onClick = {
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    startTime = LocalTime.of(hour, minute)
                                },
                                startTime.hour,
                                startTime.minute,
                                true
                            ).show()
                        }, modifier = Modifier.padding(top = 8.dp)) {
                            Text("Hora inicio: ${startTime.format(DateTimeFormatter.ofPattern("HH:mm"))}")
                        }
                        // Selector de hora de fin
                        Button(onClick = {
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    endTime = LocalTime.of(hour, minute)
                                },
                                endTime.hour,
                                endTime.minute,
                                true
                            ).show()
                        }, modifier = Modifier.padding(top = 8.dp)) {
                            Text("Hora fin: ${endTime.format(DateTimeFormatter.ofPattern("HH:mm"))}")
                        }
                        if (error.isNotEmpty()) {
                            Text(error, color = Color.Red)
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        try {
                            var start = java.time.LocalDateTime.of(selectedDay, startTime)
                            // Si la hora de fin es menor o igual que la de inicio, la fecha de fin es el día siguiente
                            val endDate = if (endTime <= startTime) selectedDay.plusDays(1) else selectedDay
                            val end = java.time.LocalDateTime.of(endDate, endTime)
                            if (title.isBlank()) {
                                error = "El título no puede estar vacío"
                            } else if (end.isBefore(start)) {
                                error = "La hora de fin debe ser después de la de inicio"
                            } else {
                                if (currentEditingTask == null) {
                                    viewModel.addTask(
                                        Task(
                                            id = System.currentTimeMillis(),
                                            title = title,
                                            start = start,
                                            end = end
                                        )
                                    )
                                } else {
                                    viewModel.updateTask(
                                        currentEditingTask.copy(
                                            title = title,
                                            start = start,
                                            end = end
                                        )
                                    )
                                }
                                showDialog = false
                            }
                        } catch (e: Exception) {
                            error = "Error al seleccionar la hora"
                        }
                    }) {
                        Text(if (currentEditingTask == null) "Guardar" else "Actualizar")
                    }
                },
                dismissButton = {
                    Row {
                        if (currentEditingTask != null) {
                            Button(onClick = {
                                viewModel.removeTask(currentEditingTask.id)
                                showDialog = false
                            }) {
                                Text("Eliminar")
                            }
                        }
                        Button(onClick = { showDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                }
            )
        }
    }
}

// Devuelve los días de la semana actual (de lunes a domingo)
private fun getCurrentWeekDays(): List<LocalDate> {
    val today = LocalDate.now()
    val startOfWeek = today.minusDays((today.dayOfWeek.value - 1).toLong())
    return (0..6).map { startOfWeek.plusDays(it.toLong()) }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TaskCard(
    task: Task,
    onToggle: () -> Unit,
    formatter: DateTimeFormatter,
    onLongPress: () -> Unit
) {
    val backgroundColor = when {
        task.isDone && task.completedInTime -> Color(0xFFB9F6CA) // Verde claro
        task.isDone && !task.completedInTime -> Color(0xFFFFCDD2) // Rojo suave
        else -> Color(0xFFF5F5F5) // Fondo más suave para pendientes
    }
    val borderColor = when {
        task.isDone && task.completedInTime -> Color(0xFF00C853) // Verde
        task.isDone && !task.completedInTime -> Color(0xFFD50000) // Rojo
        else -> Color(0xFF1976D2) // Azul para pendientes
    }
    val textColor = if (task.isDone) Color.Gray else Color.Black
    val textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None

    Card(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = onLongPress
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.ui.Modifier.background(borderColor).let { null } // No border param, so skip
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = task.title,
                    color = textColor,
                    textDecoration = textDecoration,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
                Checkbox(
                    checked = task.isDone,
                    onCheckedChange = { onToggle() }
                )
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Schedule,
                    contentDescription = "Hora de inicio",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(
                    text = task.start.format(formatter),
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
                Text(
                    text = "  -  ",
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge
                )
                // Mostrar 'día siguiente' si la fecha de fin es distinta a la de inicio
                Text(
                    text = buildString {
                        append(task.end.format(formatter))
                        if (task.end.toLocalDate() != task.start.toLocalDate()) {
                            append(" (día siguiente)")
                        }
                    },
                    color = textColor,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
                )
            }
        }
    }
}
