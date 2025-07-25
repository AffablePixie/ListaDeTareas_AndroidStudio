package com.example.timeblocker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.timeblocker.ui.TimeBlockerApp
import com.example.timeblocker.ui.theme.TimeBlockerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TimeBlockerTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    TimeBlockerApp()
                }
            }
        }
    }
}
