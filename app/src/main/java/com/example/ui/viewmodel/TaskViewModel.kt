package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.data.repository.TaskRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AppTab(val title: String) {
  BERANDA("Beranda"),
  TAMBAH("Tambah"),
  DAFTAR("Daftar Tugas"),
  PENGINGAT("Pengingat")
}

enum class FilterStatus(val label: String) {
  SEMUA("Semua"),
  PENDING("Pending"),
  MENDESAK("Mendesak"),
  SELESAI("Selesai")
}

enum class ThemeMode {
  LIGHT,
  DARK,
  SYSTEM
}

enum class BranchSimMode {
  AUTO,
  URGENT,
  SAFE
}

data class NotificationSettings(
  val h24Enabled: Boolean = true,
  val h3Enabled: Boolean = true,
  val h1Enabled: Boolean = true,
  val ringtone: String = "Chime Kampus Akademik",
  val vibrationEnabled: Boolean = true
)

class TaskViewModel(application: Application) : AndroidViewModel(application) {
  private val repository: TaskRepository
  private val prefs = application.getSharedPreferences("taskmate_prefs", Context.MODE_PRIVATE)

  // Student Profile state
  private val _studentName = MutableStateFlow(prefs.getString("student_name", "") ?: "")
  val studentName: StateFlow<String> = _studentName.asStateFlow()

  private val _studentNim = MutableStateFlow(prefs.getString("student_nim", "") ?: "")
  val studentNim: StateFlow<String> = _studentNim.asStateFlow()

  private val _studentEmail = MutableStateFlow(prefs.getString("student_email", "") ?: "")
  val studentEmail: StateFlow<String> = _studentEmail.asStateFlow()

  private val _studentMajor = MutableStateFlow(prefs.getString("student_major", "") ?: "")
  val studentMajor: StateFlow<String> = _studentMajor.asStateFlow()

  init {
    val database = AppDatabase.getDatabase(application)
    repository = TaskRepository(database.taskDao())
  }

  val allTasks: StateFlow<List<TaskEntity>> = repository.allTasks.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  private val _activeTab = MutableStateFlow(AppTab.BERANDA)
  val activeTab: StateFlow<AppTab> = _activeTab.asStateFlow()

  private val _themeMode = MutableStateFlow(ThemeMode.LIGHT)
  val themeMode: StateFlow<ThemeMode> = _themeMode.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _filterStatus = MutableStateFlow(FilterStatus.SEMUA)
  val filterStatus: StateFlow<FilterStatus> = _filterStatus.asStateFlow()

  private val _selectedSubject = MutableStateFlow<String?>(null)
  val selectedSubject: StateFlow<String?> = _selectedSubject.asStateFlow()

  private val _branchMode = MutableStateFlow(BranchSimMode.AUTO)
  val branchMode: StateFlow<BranchSimMode> = _branchMode.asStateFlow()

  private val _notificationSettings = MutableStateFlow(NotificationSettings())
  val notificationSettings: StateFlow<NotificationSettings> = _notificationSettings.asStateFlow()

  // Real-time ticking system clock
  private val _currentTimeMillis = MutableStateFlow(System.currentTimeMillis())
  val currentTimeMillis: StateFlow<Long> = _currentTimeMillis.asStateFlow()

  private val _systemClockText = MutableStateFlow("")
  val systemClockText: StateFlow<String> = _systemClockText.asStateFlow()

  // Success dialog state for adding task
  private val _recentlyAddedTask = MutableStateFlow<TaskEntity?>(null)
  val recentlyAddedTask: StateFlow<TaskEntity?> = _recentlyAddedTask.asStateFlow()

  // General feedback message (Snackbar)
  private val _toastMessage = MutableStateFlow<String?>(null)
  val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

  init {
    // Clock ticker every second
    viewModelScope.launch {
      val timeFormat = SimpleDateFormat("HH:mm:ss 'WIB'", Locale("id", "ID"))
      while (true) {
        val now = System.currentTimeMillis()
        _currentTimeMillis.value = now
        _systemClockText.value = timeFormat.format(Date(now))
        delay(1000)
      }
    }
  }

  // Filtered task list
  val filteredTasks: StateFlow<List<TaskEntity>> = combine(
    allTasks,
    _searchQuery,
    _filterStatus,
    _selectedSubject,
    _currentTimeMillis
  ) { tasks, query, status, subject, now ->
    tasks.filter { task ->
      val matchesQuery = query.isBlank() ||
          task.title.contains(query, ignoreCase = true) ||
          task.subject.contains(query, ignoreCase = true) ||
          task.notes.contains(query, ignoreCase = true)

      val matchesSubject = subject == null || task.subject == subject

      val matchesStatus = when (status) {
        FilterStatus.SEMUA -> true
        FilterStatus.PENDING -> !task.isCompleted
        FilterStatus.SELESAI -> task.isCompleted
        FilterStatus.MENDESAK -> {
          if (task.isCompleted) false
          else {
            val diffHours = (task.dueEpochMillis - now) / (1000 * 60 * 60)
            diffHours in 0..24 || task.priority == TaskPriority.TINGGI
          }
        }
      }

      matchesQuery && matchesSubject && matchesStatus
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun updateStudentProfile(name: String, nim: String, email: String, major: String) {
    _studentName.value = name.trim()
    _studentNim.value = nim.trim()
    _studentEmail.value = email.trim()
    _studentMajor.value = major.trim()

    prefs.edit()
      .putString("student_name", name.trim())
      .putString("student_nim", nim.trim())
      .putString("student_email", email.trim())
      .putString("student_major", major.trim())
      .apply()

    _toastMessage.value = if (name.isNotBlank()) "Profil berhasil disimpan, halo $name!" else "Profil diperbarui."
  }

  fun getInitials(): String {
    val name = _studentName.value.trim()
    if (name.isBlank()) return "M"
    val parts = name.split(" ").filter { it.isNotBlank() }
    return when {
      parts.size >= 2 -> "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
      parts.isNotEmpty() -> parts[0].take(2).uppercase()
      else -> "M"
    }
  }

  fun setActiveTab(tab: AppTab) {
    _activeTab.value = tab
  }

  fun setThemeMode(mode: ThemeMode) {
    _themeMode.value = mode
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setFilterStatus(status: FilterStatus) {
    _filterStatus.value = status
  }

  fun setSelectedSubject(subject: String?) {
    _selectedSubject.value = subject
  }

  fun setBranchMode(mode: BranchSimMode) {
    _branchMode.value = mode
  }

  fun clearToastMessage() {
    _toastMessage.value = null
  }

  fun clearRecentlyAddedTask() {
    _recentlyAddedTask.value = null
  }

  fun toggleTaskCompletion(task: TaskEntity) {
    viewModelScope.launch {
      val updated = !task.isCompleted
      repository.setTaskCompleted(task.id, updated)
      _toastMessage.value = if (updated) {
        "Hebat! Tugas '${task.title}' ditandai selesai! 🎉"
      } else {
        "Tugas '${task.title}' dikembalikan ke pending."
      }
    }
  }

  fun deleteTask(task: TaskEntity) {
    viewModelScope.launch {
      repository.deleteTask(task)
      _toastMessage.value = "Tugas '${task.title}' dihapus."
    }
  }

  fun snoozeTask(task: TaskEntity, minutes: Int = 30) {
    viewModelScope.launch {
      val newDueMillis = task.dueEpochMillis + (minutes * 60 * 1000L)
      val sdfDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
      val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
      val newDate = sdfDate.format(Date(newDueMillis))
      val newTime = sdfTime.format(Date(newDueMillis))

      val updated = task.copy(
        dueEpochMillis = newDueMillis,
        dueDate = newDate,
        dueTime = newTime
      )
      repository.updateTask(updated)
      _toastMessage.value = "Alarm ditunda $minutes menit untuk '${task.title}'."
    }
  }

  fun submitTask(
    title: String,
    subject: String,
    dateString: String,
    timeString: String,
    priority: TaskPriority,
    notes: String,
    alarmEnabled: Boolean
  ): Boolean {
    if (title.isBlank() || subject.isBlank() || dateString.isBlank() || timeString.isBlank()) {
      return false
    }

    val epochMillis = try {
      val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
      sdf.parse("$dateString $timeString")?.time ?: System.currentTimeMillis()
    } catch (e: Exception) {
      System.currentTimeMillis() + 86400000L
    }

    val newTask = TaskEntity(
      title = title.trim(),
      subject = subject.trim(),
      dueDate = dateString,
      dueTime = timeString,
      dueEpochMillis = epochMillis,
      priority = priority,
      notes = notes.trim(),
      alarmEnabled = alarmEnabled,
      isCompleted = false
    )

    viewModelScope.launch {
      val id = repository.insertTask(newTask)
      _recentlyAddedTask.value = newTask.copy(id = id)
      _toastMessage.value = "Tugas dan deadline berhasil disimpan ke database!"
    }
    return true
  }

  fun updateNotificationSettings(
    h24: Boolean? = null,
    h3: Boolean? = null,
    h1: Boolean? = null,
    ringtone: String? = null,
    vibration: Boolean? = null
  ) {
    val current = _notificationSettings.value
    _notificationSettings.value = current.copy(
      h24Enabled = h24 ?: current.h24Enabled,
      h3Enabled = h3 ?: current.h3Enabled,
      h1Enabled = h1 ?: current.h1Enabled,
      ringtone = ringtone ?: current.ringtone,
      vibrationEnabled = vibration ?: current.vibrationEnabled
    )
  }

  fun clearAllTasks() {
    viewModelScope.launch {
      repository.clearAll()
      _toastMessage.value = "Semua tugas telah dikosongkan."
    }
  }
}
