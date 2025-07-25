// File: app/src/main/java/com/example/timeblocker/ui/calendar/TaskCalendarScreen.kt
package com.example.timeblocker.ui.calendar

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TaskCalendarScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Vista semanal (pendiente de implementar)")
        Spacer(Modifier.height(8.dp))
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Placeholder para grid de calendario
        }
    }
}
