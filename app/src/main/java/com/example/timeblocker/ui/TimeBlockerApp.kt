// File: app/src/main/java/com/example/timeblocker/ui/TimeBlockerApp.kt
package com.example.timeblocker.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.timeblocker.ui.list.TaskListScreen
import com.example.timeblocker.ui.calendar.TaskCalendarScreen

sealed class Screen(val route: String, val icon: ImageVector, val label: String) {
    object ListView : Screen("list", Icons.Filled.List, "Lista")
    object CalendarView : Screen("calendar", Icons.Filled.DateRange, "Calendario")
}

@Composable
fun TimeBlockerApp() {
    val navController = rememberNavController()
    val screens = listOf(Screen.ListView, Screen.CalendarView)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navEntry?.destination?.route
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            navController.navigate(screen.route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.findStartDestination().route ?: screen.route) {
                                    saveState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.ListView.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.ListView.route) {
                TaskListScreen()
            }
            composable(Screen.CalendarView.route) {
                TaskCalendarScreen()
            }
        }
    }
}