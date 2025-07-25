// File: app/src/main/java/com/example/timeblocker/ui/list/TaskListScreen.kt
package com.example.timeblocker.ui.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.timeblocker.ui.list.TaskListScreenViewModel

@Composable
fun TaskListScreen(viewModel: TaskListScreenViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val tasks by viewModel.tasks.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { viewModel.showDialog() }) {
            Text("Añadir tarea")
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn {
            items(tasks) { task ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation()) {
                    Row(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(task.title)
                            Text("${task.start} - ${task.end}")
                        }
                        Checkbox(
                            checked = task.isDone,
                            onCheckedChange = { viewModel.toggleTask(task.id) }
                        )
                    }
                }
            }
        }
    }
}