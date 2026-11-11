package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.ui.theme.SecondaryFixed
import com.example.ui.theme.OnSecondaryFixed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.FilterStatus
import com.example.ui.viewmodel.TaskViewModel
import com.example.util.DateUtils

@Composable
fun DaftarTugasScreen(
  viewModel: TaskViewModel,
  onNavigateToTab: (AppTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val allTasks by viewModel.allTasks.collectAsState()
  val filteredTasks by viewModel.filteredTasks.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val filterStatus by viewModel.filterStatus.collectAsState()
  val selectedSubject by viewModel.selectedSubject.collectAsState()
  val currentTime by viewModel.currentTimeMillis.collectAsState()

  val totalCount = allTasks.size
  val completedCount = allTasks.count { it.isCompleted }
  val pendingCount = allTasks.count { !it.isCompleted }
  val urgentCount = allTasks.count { !it.isCompleted && (DateUtils.isUrgent(it.dueEpochMillis, currentTime) || it.priority == TaskPriority.TINGGI) }

  val progressPercent = if (totalCount > 0) ((completedCount.toFloat() / totalCount) * 100).toInt() else 0

  var taskToDelete by remember { mutableStateOf<TaskEntity?>(null) }

  // Unique subject list
  val subjects = remember(allTasks) {
    allTasks.map { it.subject }.distinct().filter { it.isNotBlank() }
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Momentum Summary Banner
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.fillMaxWidth().testTag("momentum_banner")
      ) {
        Row(
          modifier = Modifier.padding(18.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Surface(
              shape = CircleShape,
              color = Color.White.copy(alpha = 0.2f)
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onPrimary,
                  modifier = Modifier.size(13.dp)
                )
                Text(
                  text = "Semester Ganjil 2024/2025",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                  color = MaterialTheme.colorScheme.onPrimary
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = if (pendingCount > 0) "$pendingCount Tugas Belum Selesai" else "Tidak Ada Tugas Tertunda",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onPrimary
            )

            Text(
              text = if (pendingCount > 0) "Tuntaskan sesuai prioritas deadline untuk menjaga IPK tetap optimal." else "Bagus sekali! Semua tugas telah selesai atau belum ada tugas aktif saat ini.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          // Circular progress
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(56.dp)
          ) {
            CircularProgressIndicator(
              progress = { if (totalCount > 0) completedCount.toFloat() / totalCount else 0f },
              modifier = Modifier.fillMaxSize(),
              color = SecondaryFixed,
              trackColor = Color.White.copy(alpha = 0.2f),
              strokeWidth = 5.dp
            )
            Text(
              text = "$progressPercent%",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onPrimary
            )
          }
        }
      }
    }

    // 2. Search Bar
    item {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { viewModel.setSearchQuery(it) },
        placeholder = { Text("Cari tugas, topik, atau mata kuliah...", style = MaterialTheme.typography.bodyMedium) },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = "Cari", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { viewModel.setSearchQuery("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Hapus pencarian", tint = MaterialTheme.colorScheme.outline)
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
          focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
          unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
          focusedBorderColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.fillMaxWidth().testTag("search_tasks_input")
      )
    }

    // 3. Quick Pill Category Filters
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Semua Matkul chip
        FilterPill(
          label = "Semua Matkul",
          icon = Icons.Default.MenuBook,
          isSelected = selectedSubject == null,
          onClick = { viewModel.setSelectedSubject(null) }
        )

        // Prioritas Tinggi filter
        FilterPill(
          label = "Prioritas Tinggi",
          icon = Icons.Default.Bolt,
          isSelected = filterStatus == FilterStatus.MENDESAK,
          activeContainerColor = MaterialTheme.colorScheme.errorContainer,
          activeContentColor = MaterialTheme.colorScheme.onErrorContainer,
          onClick = {
            if (filterStatus == FilterStatus.MENDESAK) {
              viewModel.setFilterStatus(FilterStatus.SEMUA)
            } else {
              viewModel.setFilterStatus(FilterStatus.MENDESAK)
            }
          }
        )

        // Subjects chips
        subjects.forEach { subj ->
          FilterPill(
            label = subj,
            isSelected = selectedSubject == subj,
            onClick = {
              if (selectedSubject == subj) viewModel.setSelectedSubject(null) else viewModel.setSelectedSubject(subj)
            }
          )
        }
      }
    }

    // 4. Status Tabs
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(4.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          StatusTabItem(
            label = "Semua ($totalCount)",
            isSelected = filterStatus == FilterStatus.SEMUA,
            onClick = { viewModel.setFilterStatus(FilterStatus.SEMUA) },
            modifier = Modifier.weight(1f)
          )
          StatusTabItem(
            label = "Pending ($pendingCount)",
            isSelected = filterStatus == FilterStatus.PENDING,
            onClick = { viewModel.setFilterStatus(FilterStatus.PENDING) },
            modifier = Modifier.weight(1f)
          )
          StatusTabItem(
            label = "Mendesak ($urgentCount)",
            isSelected = filterStatus == FilterStatus.MENDESAK,
            onClick = { viewModel.setFilterStatus(FilterStatus.MENDESAK) },
            modifier = Modifier.weight(1f)
          )
          StatusTabItem(
            label = "Selesai ($completedCount)",
            isSelected = filterStatus == FilterStatus.SELESAI,
            onClick = { viewModel.setFilterStatus(FilterStatus.SELESAI) },
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 5. Active Task List or Empty State
    if (filteredTasks.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
          border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
          modifier = Modifier.fillMaxWidth().testTag("empty_task_list_card")
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
              Icon(
                imageVector = Icons.Default.TaskAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
              )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "Belum Ada Tugas Pending",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = if (searchQuery.isNotEmpty()) "Tidak ada tugas yang sesuai dengan pencarian '$searchQuery'." else "Semua tugasmu sudah selesai atau kamu belum menambahkan tugas baru.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = { onNavigateToTab(AppTab.TAMBAH) },
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Tambah Tugas Baru")
            }
          }
        }
      }
    } else {
      items(filteredTasks, key = { it.id }) { task ->
        TaskListItemCard(
          task = task,
          currentTime = currentTime,
          onToggleDone = { viewModel.toggleTaskCompletion(task) },
          onDelete = { taskToDelete = task }
        )
      }
    }

    // 6. Bottom Status Indicator
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
            )
            Text(
              text = "Daftar Tugas Ditampilkan (${filteredTasks.size} tugas)",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }

  // Delete Confirmation Dialog
  taskToDelete?.let { task ->
    AlertDialog(
      onDismissRequest = { taskToDelete = null },
      title = { Text("Hapus Tugas?") },
      text = { Text("Apakah Anda yakin ingin menghapus tugas '${task.title}'?") },
      confirmButton = {
        Button(
          onClick = {
            viewModel.deleteTask(task)
            taskToDelete = null
          },
          colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
          )
        ) {
          Text("Hapus")
        }
      },
      dismissButton = {
        TextButton(onClick = { taskToDelete = null }) {
          Text("Batal")
        }
      }
    )
  }
}

@Composable
fun TaskListItemCard(
  task: TaskEntity,
  currentTime: Long,
  onToggleDone: () -> Unit,
  onDelete: () -> Unit
) {
  val priorityColor = when (task.priority) {
    TaskPriority.TINGGI -> MaterialTheme.colorScheme.error
    TaskPriority.SEDANG -> MaterialTheme.colorScheme.tertiary
    TaskPriority.RENDAH -> MaterialTheme.colorScheme.secondary
  }

  val isUrgent = DateUtils.isUrgent(task.dueEpochMillis, currentTime)

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .testTag("task_item_${task.id}")
  ) {
    Column(
      modifier = Modifier.padding(14.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Top row: Subject, Priority, Alarm & Delete button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          modifier = Modifier.weight(1f)
        ) {
          Surface(
            shape = CircleShape,
            color = SecondaryFixed
          ) {
            Text(
              text = task.subject,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
              color = OnSecondaryFixed,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }

          Surface(
            shape = CircleShape,
            color = priorityColor.copy(alpha = 0.15f)
          ) {
            Text(
              text = task.priority.label,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
              color = priorityColor,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (task.alarmEnabled && !task.isCompleted) {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Alarm,
                  contentDescription = "Alarm Aktif",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(13.dp)
                )
                Text(
                  text = "Alarm",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }

          IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Hapus",
              tint = MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      // Title & Checkbox
      Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Box(
          contentAlignment = Alignment.Center,
          modifier = Modifier
            .padding(top = 2.dp)
            .size(24.dp)
            .clip(CircleShape)
            .border(
              width = 2.dp,
              color = if (task.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
              shape = CircleShape
            )
            .background(if (task.isCompleted) MaterialTheme.colorScheme.secondary else Color.Transparent)
            .clickable(onClick = onToggleDone)
            .testTag("checkbox_task_${task.id}")
        ) {
          if (task.isCompleted) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSecondary,
              modifier = Modifier.size(16.dp)
            )
          }
        }

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = task.title,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
            ),
            color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
          )

          if (task.notes.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = task.notes,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 2,
              overflow = TextOverflow.Ellipsis
            )
          }
        }
      }

      // Bottom Deadline row
      Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.CalendarToday,
              contentDescription = null,
              tint = if (isUrgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(14.dp)
            )
            Text(
              text = "${DateUtils.formatShortDate(task.dueDate)} • ${task.dueTime} WIB",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          Text(
            text = if (task.isCompleted) "Selesai" else DateUtils.formatRelativeDeadline(task.dueEpochMillis, currentTime),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = if (task.isCompleted) MaterialTheme.colorScheme.secondary else if (isUrgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
          )
        }
      }
    }
  }
}

@Composable
private fun FilterPill(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
  activeContainerColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh,
  activeContentColor: Color = MaterialTheme.colorScheme.onSurface,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = CircleShape,
    color = if (isSelected) activeContainerColor else MaterialTheme.colorScheme.surfaceContainer,
    border = BorderStroke(
      width = 1.dp,
      color = if (isSelected) MaterialTheme.colorScheme.outlineVariant else Color.Transparent
    ),
    modifier = modifier.clickable(onClick = onClick)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp),
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
      if (icon != null) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (isSelected) activeContentColor else MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.size(15.dp)
        )
      }
      Text(
        text = label,
        style = MaterialTheme.typography.labelMedium.copy(
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        ),
        color = if (isSelected) activeContentColor else MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun StatusTabItem(
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = if (isSelected) MaterialTheme.colorScheme.surfaceContainerLowest else Color.Transparent,
    shadowElevation = if (isSelected) 1.dp else 0.dp,
    modifier = modifier.clickable(onClick = onClick)
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
      ),
      color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
      textAlign = androidx.compose.ui.text.style.TextAlign.Center,
      modifier = Modifier.padding(vertical = 8.dp),
      maxLines = 1,
      overflow = TextOverflow.Ellipsis
    )
  }
}
