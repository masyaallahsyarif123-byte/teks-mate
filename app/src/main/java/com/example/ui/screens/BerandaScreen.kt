package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.OnPrimaryFixed
import com.example.ui.theme.TertiaryFixed
import com.example.ui.theme.TertiaryFixedDim
import com.example.ui.theme.OnTertiaryFixed
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.TaskViewModel
import com.example.ui.viewmodel.ThemeMode
import com.example.util.DateUtils

@Composable
fun BerandaScreen(
  viewModel: TaskViewModel,
  onNavigateToTab: (AppTab) -> Unit,
  onOpenProfile: () -> Unit,
  modifier: Modifier = Modifier
) {
  val allTasks by viewModel.allTasks.collectAsState()
  val currentTime by viewModel.currentTimeMillis.collectAsState()
  val themeMode by viewModel.themeMode.collectAsState()
  val studentName by viewModel.studentName.collectAsState()
  val studentEmail by viewModel.studentEmail.collectAsState()

  val pendingTasks = remember(allTasks) { allTasks.filter { !it.isCompleted } }
  val urgentTasks = remember(pendingTasks, currentTime) {
    pendingTasks.filter { DateUtils.isUrgent(it.dueEpochMillis, currentTime) || it.priority == TaskPriority.TINGGI }
  }
  val todayTasks = remember(pendingTasks, currentTime) {
    pendingTasks.filter { DateUtils.isToday(it.dueEpochMillis) || DateUtils.isUrgent(it.dueEpochMillis, currentTime) }
  }

  var quickSearchText by remember { mutableStateOf("") }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Greeting Banner & Stats Summary
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth().testTag("greeting_banner")
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.CalendarToday,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(15.dp)
                )
                Text(
                  text = DateUtils.formatIndonesianDate(currentTime).uppercase(),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                  ),
                  color = MaterialTheme.colorScheme.primary
                )
              }

              Spacer(modifier = Modifier.height(4.dp))

              val displayName = if (studentName.isNotBlank()) studentName else "Mahasiswa"
              Text(
                text = "Halo, $displayName! 👋",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.clickable { onOpenProfile() }
              )

              Text(
                text = if (studentName.isNotBlank()) "Semangat kuliahnya, tuntaskan progresmu hari ini tanpa stres." else "Ketuk di sini untuk mengatur nama & profil kuliahmu.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.clickable { onOpenProfile() }
              )
            }

            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(PrimaryFixed)
            ) {
              Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
              )
            }
          }

          // Quick Stats Grid
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Pending Tasks Card
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
              elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
              modifier = Modifier
                .weight(1f)
                .clickable { onNavigateToTab(AppTab.DAFTAR) }
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PrimaryFixed)
                ) {
                  Icon(
                    imageVector = Icons.Default.PendingActions,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                  )
                }
                Column {
                  Text(
                    text = "${pendingTasks.size}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "Tugas Pending",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }

            // Urgent / Deadline Dekat Card
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
              elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
              modifier = Modifier
                .weight(1f)
                .clickable { onNavigateToTab(AppTab.PENGINGAT) }
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.errorContainer)
                ) {
                  Icon(
                    imageVector = Icons.Default.Alarm,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(22.dp)
                  )
                }
                Column {
                  Text(
                    text = "${urgentTasks.size}",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (urgentTasks.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "Deadline Dekat",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }
          }
        }
      }
    }

    // 2. Menu Utama TaskMate (Flowchart Cards)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Menu Utama TaskMate",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Pilih Modul Alur",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        // Fitur 1: Tambah Tugas & Deadline
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToTab(AppTab.TAMBAH) }
            .testTag("menu_tambah_tugas")
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
            ) {
              Icon(
                imageVector = Icons.Default.PostAdd,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(26.dp)
              )
            }

            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = "Tambah Tugas & Deadline",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                  shape = CircleShape,
                  color = PrimaryFixed
                ) {
                  Text(
                    text = "Fitur 1",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              Text(
                text = "Input judul, matkul, tanggal, jam batas, prioritas, & alarm",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }

            Icon(
              imageVector = Icons.Default.ChevronRight,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.outline
            )
          }
        }

        // Fitur 2: Daftar Tugas Pending (With Quick Search Bar)
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth().testTag("menu_daftar_tugas")
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToTab(AppTab.DAFTAR) },
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(48.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(MaterialTheme.colorScheme.secondaryContainer)
              ) {
                Icon(
                  imageVector = Icons.Default.FormatListBulleted,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onSecondaryContainer,
                  modifier = Modifier.size(26.dp)
                )
              }

              Column(modifier = Modifier.weight(1f)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = "Daftar Tugas Pending",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondary
                  ) {
                    Text(
                      text = "Fitur 2",
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSecondary,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
                Text(
                  text = "Monitoring tugas berjalan & pencarian mata kuliah",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }

              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh
              ) {
                Text(
                  text = "${pendingTasks.size} Aktif",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }

              Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
              )
            }

            // Quick Search Input
            OutlinedTextField(
              value = quickSearchText,
              onValueChange = {
                quickSearchText = it
                viewModel.setSearchQuery(it)
              },
              placeholder = {
                Text("Cari kata kunci tugas atau mata kuliah...", style = MaterialTheme.typography.bodySmall)
              },
              leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Cari", tint = MaterialTheme.colorScheme.outline)
              },
              trailingIcon = {
                IconButton(onClick = {
                  viewModel.setSearchQuery(quickSearchText)
                  onNavigateToTab(AppTab.DAFTAR)
                }) {
                  Icon(Icons.Default.Tune, contentDescription = "Filter", tint = MaterialTheme.colorScheme.outline)
                }
              },
              singleLine = true,
              keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
              keyboardActions = KeyboardActions(onSearch = {
                viewModel.setSearchQuery(quickSearchText)
                onNavigateToTab(AppTab.DAFTAR)
              }),
              shape = RoundedCornerShape(10.dp),
              colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = MaterialTheme.colorScheme.primary
              ),
              modifier = Modifier.fillMaxWidth().testTag("quick_search_input")
            )
          }
        }

        // Fitur 3: Pengingat Deadline
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToTab(AppTab.PENGINGAT) }
            .testTag("menu_pengingat_deadline")
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(TertiaryFixed)
            ) {
              Icon(
                imageVector = Icons.Default.AlarmOn,
                contentDescription = null,
                tint = OnTertiaryFixed,
                modifier = Modifier.size(26.dp)
              )
            }

            Column(modifier = Modifier.weight(1f)) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = "Pengingat Deadline",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                  shape = CircleShape,
                  color = TertiaryFixedDim
                ) {
                  Text(
                    text = "Fitur 3",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                    color = OnTertiaryFixed,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
              Text(
                text = "Status alarm otomatis aktif & manajemen notifikasi",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }

            // Pulsing Alarm status indicator
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.secondary)
              )
              Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline
              )
            }
          }
        }
      }
    }

    // 3. Seksi Tugas Terdekat Hari Ini
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "Tugas Terdekat Hari Ini",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
              shape = CircleShape,
              color = PrimaryFixed
            ) {
              Text(
                text = "${todayTasks.size} Tugas",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = OnPrimaryFixed,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
              )
            }
          }

          TextButton(onClick = { onNavigateToTab(AppTab.DAFTAR) }) {
            Text(
              text = "Lihat Semua",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        if (todayTasks.isEmpty()) {
          // Empty state placeholder as in mockup
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth().testTag("empty_today_tasks_card")
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.surfaceContainer)
              ) {
                Icon(
                  imageVector = Icons.Default.TaskAlt,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.outline,
                  modifier = Modifier.size(28.dp)
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = "Belum Ada Tugas Terjadwal",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )

              Spacer(modifier = Modifier.height(4.dp))

              Text(
                text = "Semua tugas sudah selesai atau belum ditambahkan. Mulai catat tugas dan deadline kuliahmu sekarang!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
              )

              Spacer(modifier = Modifier.height(14.dp))

              Button(
                onClick = { onNavigateToTab(AppTab.TAMBAH) },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Tambah Tugas Baru")
              }
            }
          }
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            todayTasks.take(3).forEach { task ->
              TaskMiniItemCard(
                task = task,
                currentTime = currentTime,
                onToggleDone = { viewModel.toggleTaskCompletion(task) },
                onClick = { onNavigateToTab(AppTab.DAFTAR) }
              )
            }
          }
        }
      }
    }

    // 4. Seksi Tampilan & Tema (Interactive Theme Selector)
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            Text(
              text = "Tampilan & Tema",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Surface(
              shape = CircleShape,
              color = PrimaryFixed
            ) {
              Text(
                text = if (themeMode == ThemeMode.DARK) "Mode Gelap Aktif" else "Mode Terang Aktif",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
              )
            }
          }
          Text(
            text = "Sesuaikan Mata",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth().testTag("theme_selector_card")
        ) {
          Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(40.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(PrimaryFixed)
              ) {
                Icon(
                  imageVector = Icons.Default.Palette,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(22.dp)
                )
              }
              Column {
                Text(
                  text = "Palet Warna Tampilan",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Pilih mode nyaman untuk belajar siang atau malam",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Option Light
              val isLightSelected = themeMode != ThemeMode.DARK
              Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                  width = if (isLightSelected) 2.dp else 1.dp,
                  color = if (isLightSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                colors = CardDefaults.cardColors(
                  containerColor = if (isLightSelected) PrimaryFixed.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceContainerLow
                ),
                modifier = Modifier
                  .weight(1f)
                  .clickable { viewModel.setThemeMode(ThemeMode.LIGHT) }
                  .testTag("theme_option_light")
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      contentAlignment = Alignment.Center,
                      modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                    ) {
                      Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                      )
                    }

                    Icon(
                      imageVector = if (isLightSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                      contentDescription = null,
                      tint = if (isLightSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                      modifier = Modifier.size(20.dp)
                    )
                  }

                  Text(
                    text = "Mode Terang (Siang)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )

                  Text(
                    text = "Indigo & Bersih Cerah",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(0xFF3525CD)))
                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(0xFFFAF8FF)).border(1.dp, Color.Gray, CircleShape))
                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(0xFF006A61)))
                  }
                }
              }

              // Option Dark
              val isDarkSelected = themeMode == ThemeMode.DARK
              Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                  width = if (isDarkSelected) 2.dp else 1.dp,
                  color = if (isDarkSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                ),
                colors = CardDefaults.cardColors(
                  containerColor = if (isDarkSelected) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainerLow
                ),
                modifier = Modifier
                  .weight(1f)
                  .clickable { viewModel.setThemeMode(ThemeMode.DARK) }
                  .testTag("theme_option_dark")
              ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(
                      contentAlignment = Alignment.Center,
                      modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B))
                    ) {
                      Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                      )
                    }

                    Icon(
                      imageVector = if (isDarkSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                      contentDescription = null,
                      tint = if (isDarkSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                      modifier = Modifier.size(20.dp)
                    )
                  }

                  Text(
                    text = "Mode Gelap (Night)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )

                  Text(
                    text = "Obsidian Navy & Teduh",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(0xFF0B1120)))
                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(0xFF1E293B)))
                    Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(Color(0xFFA5B4FC)))
                  }
                }
              }
            }
          }
        }
      }
    }

    // 5. Seksi Akun Mahasiswa
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        modifier = Modifier.fillMaxWidth().testTag("student_account_card")
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
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
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            ) {
              Icon(
                imageVector = Icons.Default.ManageAccounts,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
              )
            }

            Column(modifier = Modifier.clickable { onOpenProfile() }) {
              Text(
                text = if (studentName.isNotBlank()) studentName else "Profil Mahasiswa",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = if (studentEmail.isNotBlank()) studentEmail else "Ketuk untuk melengkapi profil",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          TextButton(
            onClick = onOpenProfile,
            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
          ) {
            Icon(Icons.Default.ManageAccounts, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Atur Profil",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
            )
          }
        }
      }
    }
  }
}

@Composable
fun TaskMiniItemCard(
  task: TaskEntity,
  currentTime: Long,
  onToggleDone: () -> Unit,
  onClick: () -> Unit
) {
  val priorityColor = when (task.priority) {
    TaskPriority.TINGGI -> MaterialTheme.colorScheme.error
    TaskPriority.SEDANG -> MaterialTheme.colorScheme.tertiary
    TaskPriority.RENDAH -> MaterialTheme.colorScheme.secondary
  }

  Card(
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      // Checkbox
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .size(24.dp)
          .clip(CircleShape)
          .border(
            width = 2.dp,
            color = if (task.isCompleted) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outline,
            shape = CircleShape
          )
          .background(if (task.isCompleted) MaterialTheme.colorScheme.secondary else Color.Transparent)
          .clickable(onClick = onToggleDone)
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
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceContainerHigh
          ) {
            Text(
              text = task.subject,
              style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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

        Text(
          text = task.title,
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.SemiBold,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None
          ),
          color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Text(
          text = "${DateUtils.formatRelativeDeadline(task.dueEpochMillis, currentTime)} • ${task.dueTime} WIB",
          style = MaterialTheme.typography.labelSmall,
          color = if (DateUtils.isUrgent(task.dueEpochMillis, currentTime)) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}
