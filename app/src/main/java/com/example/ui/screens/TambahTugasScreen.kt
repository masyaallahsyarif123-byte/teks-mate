package com.example.ui.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LowPriority
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sync
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.SecondaryFixed
import com.example.ui.theme.OnSecondaryFixed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.TaskPriority
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.TaskViewModel
import com.example.util.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun TambahTugasScreen(
  viewModel: TaskViewModel,
  onNavigateToTab: (AppTab) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val recentlyAddedTask by viewModel.recentlyAddedTask.collectAsState()

  // Form states
  var title by remember { mutableStateOf("") }
  var subject by remember { mutableStateOf("") }

  val calendar = remember { Calendar.getInstance() }
  val sdfDate = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
  val sdfDisplay = remember { SimpleDateFormat("d MMMM yyyy", Locale("id", "ID")) }

  var selectedDate by remember { mutableStateOf(sdfDate.format(calendar.time)) }
  var displayDate by remember { mutableStateOf(sdfDisplay.format(calendar.time)) }
  var selectedTime by remember { mutableStateOf("23:59") }
  var priority by remember { mutableStateOf(TaskPriority.TINGGI) }
  var notes by remember { mutableStateOf("") }
  var alarmEnabled by remember { mutableStateOf(true) }

  val matkulSuggestions = listOf(
    "Sistem Informasi Basis Data",
    "Rekayasa Perangkat Lunak",
    "Algoritma & Struktur Data",
    "Kalkulus & Aljabar Linier",
    "Interaksi Manusia & Komputer",
    "Jaringan Komputer & Keamanan"
  )

  val isFormValid = title.trim().length >= 3 && subject.trim().isNotBlank()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Flowchart Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            Icon(
              imageVector = Icons.Default.PostAdd,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
            Text(
              text = "FLOWCHART FITUR 1",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
              color = MaterialTheme.colorScheme.primary
            )
          }
          Text(
            text = "Detail Penugasan Baru",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Surface(
          shape = CircleShape,
          color = MaterialTheme.colorScheme.secondaryContainer
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Sync,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSecondaryContainer,
              modifier = Modifier.size(16.dp)
            )
            Text(
              text = "Auto Sync",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
              color = MaterialTheme.colorScheme.onSecondaryContainer
            )
          }
        }
      }
    }

    // Motivational Banner
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.primary)
          ) {
            Icon(
              imageVector = Icons.Default.School,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onPrimary,
              modifier = Modifier.size(24.dp)
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Tetap Teratur & Bebas Panik",
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Sistem TaskMate otomatis menjadwalkan alarm & kalender perkuliahanmu.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
    }

    // 1. Judul Tugas
    item {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Judul Tugas *",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "${title.length}/80",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
          )
        }

        OutlinedTextField(
          value = title,
          onValueChange = { if (it.length <= 80) title = it },
          placeholder = { Text("Misal: Laporan Praktikum Basis Data") },
          leadingIcon = {
            Icon(Icons.Default.Assignment, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            focusedBorderColor = MaterialTheme.colorScheme.primary
          ),
          modifier = Modifier.fillMaxWidth().testTag("input_task_title")
        )
      }
    }

    // 2. Mata Kuliah
    item {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
          text = "Mata Kuliah *",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
          value = subject,
          onValueChange = { subject = it },
          placeholder = { Text("Misal: Rekayasa Perangkat Lunak") },
          leadingIcon = {
            Icon(Icons.Default.MenuBook, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            focusedBorderColor = MaterialTheme.colorScheme.primary
          ),
          modifier = Modifier.fillMaxWidth().testTag("input_task_subject")
        )

        // Suggestion Chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          matkulSuggestions.forEach { matkul ->
            Surface(
              shape = CircleShape,
              color = if (subject == matkul) PrimaryFixed else MaterialTheme.colorScheme.surfaceContainerLow,
              border = BorderStroke(
                width = 1.dp,
                color = if (subject == matkul) MaterialTheme.colorScheme.primary else Color.Transparent
              ),
              modifier = Modifier.clickable { subject = matkul }
            ) {
              Text(
                text = matkul,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = if (subject == matkul) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }
      }
    }

    // 3. Tanggal & Jam Pengumpulan
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Date Picker Field
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "Tanggal Tenggat *",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                val cal = Calendar.getInstance()
                DatePickerDialog(
                  context,
                  { _, year, month, dayOfMonth ->
                    val chosen = Calendar.getInstance().apply {
                      set(year, month, dayOfMonth)
                    }
                    selectedDate = sdfDate.format(chosen.time)
                    displayDate = sdfDisplay.format(chosen.time)
                  },
                  cal.get(Calendar.YEAR),
                  cal.get(Calendar.MONTH),
                  cal.get(Calendar.DAY_OF_MONTH)
                ).show()
              }
              .testTag("picker_deadline_date")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = displayDate,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
              )
            }
          }
        }

        // Time Picker Field
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text(
            text = "Jam Batas *",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                val cal = Calendar.getInstance()
                TimePickerDialog(
                  context,
                  { _, hourOfDay, minute ->
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                  },
                  cal.get(Calendar.HOUR_OF_DAY),
                  cal.get(Calendar.MINUTE),
                  true
                ).show()
              }
              .testTag("picker_deadline_time")
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = "$selectedTime WIB",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
              )
            }
          }
        }
      }
    }

    // 4. Tingkat Prioritas
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Tingkat Prioritas *",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          val priorityColor = when (priority) {
            TaskPriority.TINGGI -> MaterialTheme.colorScheme.error
            TaskPriority.SEDANG -> MaterialTheme.colorScheme.tertiary
            TaskPriority.RENDAH -> MaterialTheme.colorScheme.secondary
          }

          Text(
            text = "${priority.label} (${priority.deadlineDescription})",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = priorityColor
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          // Tinggi
          PriorityButton(
            title = "Tinggi",
            subtitle = "< 24 Jam",
            icon = Icons.Default.PriorityHigh,
            isSelected = priority == TaskPriority.TINGGI,
            activeColor = MaterialTheme.colorScheme.error,
            activeOnColor = MaterialTheme.colorScheme.onError,
            onClick = { priority = TaskPriority.TINGGI },
            modifier = Modifier.weight(1f)
          )

          // Sedang
          PriorityButton(
            title = "Sedang",
            subtitle = "1-3 Hari",
            icon = Icons.Default.HourglassTop,
            isSelected = priority == TaskPriority.SEDANG,
            activeColor = MaterialTheme.colorScheme.tertiary,
            activeOnColor = MaterialTheme.colorScheme.onTertiary,
            onClick = { priority = TaskPriority.SEDANG },
            modifier = Modifier.weight(1f)
          )

          // Rendah
          PriorityButton(
            title = "Rendah",
            subtitle = "> 3 Hari",
            icon = Icons.Default.LowPriority,
            isSelected = priority == TaskPriority.RENDAH,
            activeColor = MaterialTheme.colorScheme.secondary,
            activeOnColor = MaterialTheme.colorScheme.onSecondary,
            onClick = { priority = TaskPriority.RENDAH },
            modifier = Modifier.weight(1f)
          )
        }
      }
    }

    // 5. Catatan / Link Pengumpulan
    item {
      Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "Catatan / Link Pengumpulan",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = "Opsional",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
          )
        }

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          placeholder = {
            Text("Tuliskan format submission, tautan Google Drive / Portal Kuliah, atau instruksi dosen...")
          },
          minLines = 3,
          maxLines = 5,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
            focusedBorderColor = MaterialTheme.colorScheme.primary
          ),
          modifier = Modifier.fillMaxWidth().testTag("input_task_notes")
        )
      }
    }

    // 6. Set Alarm Pengingat Sistem
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier.fillMaxWidth().testTag("alarm_toggle_card")
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.weight(1f)
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(SecondaryFixed)
              ) {
                Icon(
                  imageVector = Icons.Default.NotificationsActive,
                  contentDescription = null,
                  tint = OnSecondaryFixed,
                  modifier = Modifier.size(20.dp)
                )
              }

              Column {
                Text(
                  text = "Set Alarm Pengingat Sistem",
                  style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Notifikasi suara & banner otomatis",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Switch(
              checked = alarmEnabled,
              onCheckedChange = { alarmEnabled = it },
              colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                checkedTrackColor = MaterialTheme.colorScheme.primary
              )
            )
          }

          AnimatedVisibility(visible = alarmEnabled) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              modifier = Modifier.padding(top = 4.dp)
            ) {
              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                  )
                  Text(
                    text = "1 Hari Sebelum",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
              }

              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp),
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(14.dp)
                  )
                  Text(
                    text = "3 Jam Sebelum",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }
              }
            }
          }
        }
      }
    }

    // Submit Button
    item {
      Button(
        onClick = {
          if (isFormValid) {
            viewModel.submitTask(
              title = title,
              subject = subject,
              dateString = selectedDate,
              timeString = selectedTime,
              priority = priority,
              notes = notes,
              alarmEnabled = alarmEnabled
            )
          }
        },
        enabled = isFormValid,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(54.dp)
          .testTag("submit_task_button")
      ) {
        Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Simpan ke Database & Set Alarm",
          style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
        )
      }
    }
  }

  // Success Dialog Modal
  recentlyAddedTask?.let { task ->
    Dialog(onDismissRequest = { viewModel.clearRecentlyAddedTask() }) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest,
        shadowElevation = 10.dp,
        modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("success_modal")
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(SecondaryFixed)
          ) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = null,
              tint = OnSecondaryFixed,
              modifier = Modifier.size(36.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Text(
            text = "Tugas dan Deadline Berhasil Disimpan!",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Tugas telah masuk ke antrean database lokal. Pengingat 24 jam & 3 jam sebelum deadline telah diaktifkan di perangkat Anda.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(16.dp))

          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(12.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = "Matkul:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text(
                  text = task.subject,
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface,
                  maxLines = 1
                )
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(text = "Deadline:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Text(
                  text = "${task.dueDate} • ${task.dueTime} WIB",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          Button(
            onClick = {
              viewModel.clearRecentlyAddedTask()
              onNavigateToTab(AppTab.DAFTAR)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Lihat Daftar Tugas")
          }

          Spacer(modifier = Modifier.height(8.dp))

          OutlinedButton(
            onClick = {
              viewModel.clearRecentlyAddedTask()
              title = ""
              notes = ""
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Tambah Tugas Lain")
          }
        }
      }
    }
  }
}

@Composable
private fun PriorityButton(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  isSelected: Boolean,
  activeColor: Color,
  activeOnColor: Color,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) activeColor else MaterialTheme.colorScheme.surfaceContainer
    ),
    modifier = modifier.clickable(onClick = onClick)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 12.dp, horizontal = 8.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (isSelected) activeOnColor else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = if (isSelected) activeOnColor else MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
        color = if (isSelected) activeOnColor.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
      )
    }
  }
}
