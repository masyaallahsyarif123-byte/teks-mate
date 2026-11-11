package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ProfileDialog
import com.example.ui.components.TaskMateBottomNav
import com.example.ui.components.TaskMateHeader
import com.example.ui.screens.BerandaScreen
import com.example.ui.screens.DaftarTugasScreen
import com.example.ui.screens.PengingatDeadlineScreen
import com.example.ui.screens.TambahTugasScreen
import com.example.ui.theme.TaskMateTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.TaskViewModel
import com.example.ui.viewmodel.ThemeMode

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val taskViewModel: TaskViewModel = viewModel()
      val themeMode by taskViewModel.themeMode.collectAsState()
      val systemInDark = isSystemInDarkTheme()

      val isDark = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> systemInDark
      }

      TaskMateTheme(darkTheme = isDark) {
        TaskMateApp(taskViewModel = taskViewModel)
      }
    }
  }
}

@Composable
fun TaskMateApp(taskViewModel: TaskViewModel) {
  val activeTab by taskViewModel.activeTab.collectAsState()
  val themeMode by taskViewModel.themeMode.collectAsState()
  val allTasks by taskViewModel.allTasks.collectAsState()
  val toastMessage by taskViewModel.toastMessage.collectAsState()
  val studentName by taskViewModel.studentName.collectAsState()
  val studentNim by taskViewModel.studentNim.collectAsState()
  val studentEmail by taskViewModel.studentEmail.collectAsState()
  val studentMajor by taskViewModel.studentMajor.collectAsState()

  val snackbarHostState = remember { SnackbarHostState() }
  var showProfileDialog by remember { mutableStateOf(false) }
  var showNotificationAlert by remember { mutableStateOf(false) }

  // Handle Android back button: return to Beranda tab first before exiting
  BackHandler(enabled = activeTab != AppTab.BERANDA) {
    taskViewModel.setActiveTab(AppTab.BERANDA)
  }

  // Show toast messages as Snackbar
  LaunchedEffect(toastMessage) {
    toastMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      taskViewModel.clearToastMessage()
    }
  }

  val totalCount = allTasks.size
  val completedCount = allTasks.count { it.isCompleted }

  Scaffold(
    topBar = {
      TaskMateHeader(
        currentTab = activeTab,
        themeMode = themeMode,
        studentInitials = taskViewModel.getInitials(),
        onToggleTheme = {
          if (themeMode == ThemeMode.DARK) {
            taskViewModel.setThemeMode(ThemeMode.LIGHT)
          } else {
            taskViewModel.setThemeMode(ThemeMode.DARK)
          }
        },
        onOpenNotifications = { showNotificationAlert = true },
        onOpenProfile = { showProfileDialog = true }
      )
    },
    bottomBar = {
      TaskMateBottomNav(
        currentTab = activeTab,
        onTabSelected = { tab -> taskViewModel.setActiveTab(tab) }
      )
    },
    snackbarHost = { SnackbarHost(snackbarHostState) },
    modifier = Modifier.fillMaxSize()
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)
        .padding(innerPadding)
    ) {
      AnimatedContent(
        targetState = activeTab,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "tab_switch_animation"
      ) { tab ->
        when (tab) {
          AppTab.BERANDA -> BerandaScreen(
            viewModel = taskViewModel,
            onNavigateToTab = { taskViewModel.setActiveTab(it) },
            onOpenProfile = { showProfileDialog = true }
          )
          AppTab.TAMBAH -> TambahTugasScreen(
            viewModel = taskViewModel,
            onNavigateToTab = { taskViewModel.setActiveTab(it) }
          )
          AppTab.DAFTAR -> DaftarTugasScreen(
            viewModel = taskViewModel,
            onNavigateToTab = { taskViewModel.setActiveTab(it) }
          )
          AppTab.PENGINGAT -> PengingatDeadlineScreen(
            viewModel = taskViewModel
          )
        }
      }
    }
  }

  // Profile Dialog
  if (showProfileDialog) {
    ProfileDialog(
      currentName = studentName,
      currentNim = studentNim,
      currentEmail = studentEmail,
      currentMajor = studentMajor,
      totalTasks = totalCount,
      completedTasks = completedCount,
      onDismiss = { showProfileDialog = false },
      onSaveProfile = { name, nim, email, major ->
        taskViewModel.updateStudentProfile(name, nim, email, major)
      },
      onClearTasks = { taskViewModel.clearAllTasks() }
    )
  }

  // Notifications Info Dialog
  if (showNotificationAlert) {
    AlertDialog(
      onDismissRequest = { showNotificationAlert = false },
      title = { Text("Pusat Notifikasi TaskMate") },
      text = {
        Text("Semua jadwal alarm kuliah aktif dan tersinkronisasi dengan database lokal. Anda akan menerima notifikasi suara dan getar pada H-24 Jam, H-3 Jam, dan H-1 Jam sebelum batas waktu.")
      },
      confirmButton = {
        TextButton(onClick = {
          showNotificationAlert = false
          taskViewModel.setActiveTab(AppTab.PENGINGAT)
        }) {
          Text("Atur Jadwal Alarm")
        }
      },
      dismissButton = {
        TextButton(onClick = { showNotificationAlert = false }) {
          Text("Tutup")
        }
      }
    )
  }
}
