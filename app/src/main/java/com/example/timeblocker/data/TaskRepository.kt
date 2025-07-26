package com.example.timeblocker.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.timeblocker.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.time.LocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object TaskRepository {
    private const val DATASTORE_NAME = "tasks_datastore"
    private val TASKS_KEY = stringPreferencesKey("tasks_json")
    private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private var initialized = false

    // Serializador para LocalDateTime
    object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)
        override fun serialize(encoder: Encoder, value: LocalDateTime) {
            encoder.encodeString(value.toString())
        }
        override fun deserialize(decoder: Decoder): LocalDateTime {
            return LocalDateTime.parse(decoder.decodeString())
        }
    }

    private val json = Json {
        serializersModule = SerializersModule {
            contextual(LocalDateTime::class, LocalDateTimeSerializer)
        }
    }

    fun initialize(context: Context) {
        if (initialized) return
        initialized = true
        CoroutineScope(Dispatchers.IO).launch {
            val jsonString = context.dataStore.data.map { it[TASKS_KEY] ?: "[]" }.first()
            val loaded = try {
                json.decodeFromString<List<Task>>(jsonString)
            } catch (e: Exception) {
                emptyList()
            }
            if (loaded.isEmpty()) {
                // Tareas de ejemplo solo si está vacío
                val now = java.time.LocalDateTime.now()
                val exampleTasks = listOf(
                    Task(1L, "Comprar leche", now, now.plusHours(1)),
                    Task(2L, "Llamar al médico", now.plusHours(2), now.plusHours(3)),
                    Task(3L, "Enviar informe", now.plusHours(4), now.plusHours(5))
                )
                _tasks.value = exampleTasks
            } else {
                _tasks.value = loaded
            }
        }
        // Observa cambios y guarda automáticamente
        CoroutineScope(Dispatchers.IO).launch {
            _tasks.collect { list ->
                val jsonString = json.encodeToString(list)
                context.dataStore.edit { prefs ->
                    prefs[TASKS_KEY] = jsonString
                }
            }
        }
    }

    fun addTask(task: Task) {
        _tasks.value = _tasks.value + task
    }

    fun updateTask(updated: Task) {
        println("updateTask: $updated")
        _tasks.value = _tasks.value.map { if (it.id == updated.id) updated else it }
    }

    fun removeTask(taskId: Long) {
        _tasks.value = _tasks.value.filter { it.id != taskId }
    }
}
