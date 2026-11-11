package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.FmdBad
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhonelinkRing
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.SecondaryFixed
import com.example.ui.theme.OnSecondaryFixed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import com.example.ui.viewmodel.BranchSimMode
import com.example.ui.viewmodel.TaskViewModel
import com.example.util.DateUtils

@Composable
fun PengingatDeadlineScreen(
  viewModel: TaskViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val allTasks by viewModel.allTasks.collectAsState()
  val currentTime by viewModel.currentTimeMillis.collectAsState()
  val clockText by viewModel.systemClockText.collectAsState()
  val branchMode by viewModel.branchMode.collectAsState()
  val notificationSettings by viewModel.notificationSettings.collectAsState()

  // Find nearest pending task and urgent task (< 3 hours)
  val pendingTasks = remember(allTasks) { allTasks.filter { !it.isCompleted } }
  val urgentTasks = remember(pendingTasks, currentTime) {
    pendingTasks.filter {
      val diffHours = (it.dueEpochMillis - currentTime) / (1000 * 60 * 60)
      diffHours in 0..3 || it.priority == TaskPriority.TINGGI
    }
  }

  // Current display branch based on user selection or auto
  val isUrgentBranch = when (branchMode) {
    BranchSimMode.URGENT -> true
    BranchSimMode.SAFE -> false
    BranchSimMode.AUTO -> urgentTasks.isNotEmpty()
  }

  val activeUrgentTask = urgentTasks.firstOrNull() ?: pendingTasks.firstOrNull()
  val nextSafeTask = pendingTasks.firstOrNull { it.id != activeUrgentTask?.id } ?: pendingTasks.firstOrNull()

  var showRingtoneDialog by remember { mutableStateOf(false) }
  var showSubmitModal by remember { mutableStateOf<TaskEntity?>(null) }

  // Radar ping animation
  val infiniteTransition = rememberInfiniteTransition(label = "radar")
  val pingScale by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.35f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200),
      repeatMode = RepeatMode.Restart
    ),
    label = "pingScale"
  )

  fun triggerDeviceVibration() {
    try {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        val vibrator = vibratorManager?.defaultVibrator
        vibrator?.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
      } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        @Suppress("DEPRECATION")
        vibrator?.vibrate(300)
      }
    } catch (_: Exception) {}
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    contentPadding = PaddingValues(top = 12.dp, bottom = 120.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Blok 1: Visualisasi Logika Sistem & Sensor Real-Time Sync
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().testTag("system_sync_card")
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              // Radar pulsing icon
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(36.dp)
              ) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .scale(pingScale)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                )
                Box(
                  contentAlignment = Alignment.Center,
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(PrimaryFixed)
                ) {
                  Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }

              Column {
                Text(
                  text = "Pengecekan Waktu Sistem",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(6.dp)
                      .clip(CircleShape)
                      .background(MaterialTheme.colorScheme.secondary)
                  )
                  Text(
                    text = "Sinkronisasi Server: $clockText",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }

            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.surfaceContainer
            ) {
              Text(
                text = "Algoritma Aktif",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          // Branch Logic Interactive Selector
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(4.dp),
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isUrgentBranch) MaterialTheme.colorScheme.surfaceContainerLowest else Color.Transparent,
                shadowElevation = if (isUrgentBranch) 1.dp else 0.dp,
                modifier = Modifier
                  .weight(1f)
                  .clickable { viewModel.setBranchMode(BranchSimMode.URGENT) }
                  .testTag("tab_branch_urgent")
              ) {
                Text(
                  text = "Cabang: Ada Deadline (< 3 Jam)",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isUrgentBranch) FontWeight.Bold else FontWeight.Normal
                  ),
                  color = if (isUrgentBranch) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(vertical = 8.dp)
                )
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (!isUrgentBranch) MaterialTheme.colorScheme.surfaceContainerLowest else Color.Transparent,
                shadowElevation = if (!isUrgentBranch) 1.dp else 0.dp,
                modifier = Modifier
                  .weight(1f)
                  .clickable { viewModel.setBranchMode(BranchSimMode.SAFE) }
                  .testTag("tab_branch_safe")
              ) {
                Text(
                  text = "Cabang: Jadwal Aman",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (!isUrgentBranch) FontWeight.Bold else FontWeight.Normal
                  ),
                  color = if (!isUrgentBranch) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(vertical = 8.dp)
                )
              }
            }
          }
        }
      }
    }

    // Blok 2: Percabangan 'Ada Deadline Mendekati? (TRUE)'
    if (isUrgentBranch) {
      item {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          // Urgent Alert Banner
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            modifier = Modifier.fillMaxWidth().testTag("urgent_alert_banner")
          ) {
            Row(
              modifier = Modifier.padding(16.dp),
              verticalAlignment = Alignment.Top,
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(MaterialTheme.colorScheme.error)
              ) {
                Icon(
                  imageVector = Icons.Default.Warning,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.onError,
                  modifier = Modifier.size(20.dp)
                )
              }

              Column(modifier = Modifier.weight(1f)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                  Text(
                    text = "PENGINGAT DEADLINE",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.error
                  )
                  Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.error
                  ) {
                    Text(
                      text = "Mendesak",
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onError,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                  text = "Tugas harus segera dikumpulkan! Batas toleransi pengiriman berkurang.",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onErrorContainer
                )
              }
            }
          }

          // Urgent Task Card Details
          if (activeUrgentTask != null) {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
              elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
              modifier = Modifier.fillMaxWidth().testTag("urgent_task_card")
            ) {
              Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Surface(
                    shape = CircleShape,
                    color = SecondaryFixed
                  ) {
                    Text(
                      text = activeUrgentTask.subject,
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = OnSecondaryFixed,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                  }

                  Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.AlarmOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(15.dp)
                      )
                      Text(
                        text = "Alarm Aktif",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.error
                      )
                    }
                  }
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                  Text(
                    text = activeUrgentTask.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  if (activeUrgentTask.notes.isNotBlank()) {
                    Text(
                      text = activeUrgentTask.notes,
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                // Countdown Strip
                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = MaterialTheme.colorScheme.surfaceContainerLow,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                      Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                          .size(36.dp)
                          .clip(CircleShape)
                          .background(MaterialTheme.colorScheme.errorContainer)
                      ) {
                        Icon(
                          imageVector = Icons.Default.HourglassBottom,
                          contentDescription = null,
                          tint = MaterialTheme.colorScheme.error,
                          modifier = Modifier.size(20.dp)
                        )
                      }
                      Column {
                        Text(
                          text = "Sisa Waktu Penyerahan",
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                          text = DateUtils.getRemainingTimeText(activeUrgentTask.dueEpochMillis, currentTime),
                          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                          color = MaterialTheme.colorScheme.error
                        )
                      }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                      Text(
                        text = "Tenggat Resmi",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                      )
                      Text(
                        text = "${activeUrgentTask.dueTime} WIB",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                      )
                    }
                  }
                }

                // Action Buttons
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Button(
                    onClick = { showSubmitModal = activeUrgentTask },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.weight(1f).height(46.dp).testTag("btn_kumpulkan_sekarang")
                  ) {
                    Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Kumpulkan Sekarang", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                  }

                  OutlinedButton(
                    onClick = {
                      viewModel.snoozeTask(activeUrgentTask, 30)
                      triggerDeviceVibration()
                    },
                    shape = CircleShape,
                    modifier = Modifier.weight(1f).height(46.dp).testTag("btn_tunda_30m")
                  ) {
                    Icon(Icons.Default.Snooze, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tunda 30 Menit", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                  }
                }
              }
            }
          } else {
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                Text(
                  text = "Belum Ada Tugas Mendesak",
                  style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                  text = "Tidak ada tugas dengan batas waktu kurang dari 3 jam saat ini.",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  textAlign = TextAlign.Center
                )
              }
            }
          }
        }
      }
    } else {
      // Blok 3: Percabangan 'Apakah ada deadline mendekati? (FALSE)'
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth().testTag("safe_schedule_card")
        ) {
          Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(36.dp)
              )
            }

            Surface(
              shape = CircleShape,
              color = SecondaryFixed
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Verified,
                  contentDescription = null,
                  tint = OnSecondaryFixed,
                  modifier = Modifier.size(16.dp)
                )
                Text(
                  text = "Jadwal Aman Aktif",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = OnSecondaryFixed
                )
              }
            }

            Text(
              text = "Tidak ada tugas yang mendesak",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface,
              textAlign = TextAlign.Center
            )

            Text(
              text = "Semua tugas masih memiliki waktu pengerjaan yang cukup. Sistem akan otomatis mengingatkan saat deadline mendekat.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = TextAlign.Center
            )

            if (nextSafeTask != null) {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                  ) {
                    Icon(
                      imageVector = Icons.Default.Schedule,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.secondary,
                      modifier = Modifier.size(20.dp)
                    )
                    Column {
                      Text(
                        text = "Tugas Terdekat Berikutnya",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      Text(
                        text = "${nextSafeTask.title} (${nextSafeTask.subject})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                      )
                    }
                  }

                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest
                  ) {
                    Text(
                      text = DateUtils.formatRelativeDeadline(nextSafeTask.dueEpochMillis, currentTime),
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                      color = MaterialTheme.colorScheme.secondary,
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }
    }

    // Blok 4: Pengaturan Alarm Pengingat (Rules Engine)
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth().testTag("alarm_rules_card")
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                  .size(32.dp)
                  .clip(CircleShape)
                  .background(PrimaryFixed)
              ) {
                Icon(
                  imageVector = Icons.Default.NotificationsActive,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp)
                )
              }
              Text(
                text = "Pengaturan Waktu Notifikasi",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Text(
              text = "Otomatisasi Aktif",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.secondary
            )
          }

          // Interval Switches: H-24 Jam, H-3 Jam, H-1 Jam
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // H-24 Jam
            AlarmRuleItem(
              title = "H-24 Jam Sebelum Deadline",
              subtitle = "Pengingat tahap awal & tinjauan materi",
              icon = Icons.Default.Notifications,
              iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
              isChecked = notificationSettings.h24Enabled,
              onCheckedChange = { viewModel.updateNotificationSettings(h24 = it) }
            )

            // H-3 Jam
            AlarmRuleItem(
              title = "H-3 Jam Sebelum Deadline",
              subtitle = "Persiapan ekspor file & cek format kumpul",
              icon = Icons.Default.NotificationImportant,
              iconTint = MaterialTheme.colorScheme.primary,
              isChecked = notificationSettings.h3Enabled,
              onCheckedChange = { viewModel.updateNotificationSettings(h3 = it) }
            )

            // H-1 Jam
            AlarmRuleItem(
              title = "H-1 Jam Sebelum Deadline",
              subtitle = "Peringatan darurat toleransi waktu akhir",
              icon = Icons.Default.FmdBad,
              iconTint = MaterialTheme.colorScheme.error,
              isChecked = notificationSettings.h1Enabled,
              onCheckedChange = { viewModel.updateNotificationSettings(h1 = it) }
            )
          }

          // Pilihan Nada & Saluran Pengingat
          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.surfaceContainer,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                  )
                  Column {
                    Text(
                      text = "Nada Dering Alarm",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = notificationSettings.ringtone,
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                Button(
                  onClick = { showRingtoneDialog = true },
                  shape = RoundedCornerShape(8.dp),
                  colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                    contentColor = MaterialTheme.colorScheme.primary
                  ),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Text("Ganti", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
              }
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.surfaceContainer,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.PhonelinkRing,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                  )
                  Column {
                    Text(
                      text = "Push Alert Perangkat",
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = "Getar Maksimal & Layar Hidup",
                      style = MaterialTheme.typography.bodySmall,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                  Text(
                    text = "Aktif",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }

          // Test Button
          OutlinedButton(
            onClick = {
              triggerDeviceVibration()
              viewModel.snoozeTask(activeUrgentTask ?: return@OutlinedButton, 0)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          ) {
            Icon(Icons.Default.Alarm, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Uji Bunyi Alarm & Getar Sekarang")
          }
        }
      }
    }
  }

  // Ringtone Selector Dialog
  if (showRingtoneDialog) {
    val ringtones = listOf(
      "Chime Kampus Akademik",
      "Digital Beep Kampus",
      "Gentle Bell Breeze",
      "Morning Pulse Alarm",
      "Cyber Radar Alert"
    )

    AlertDialog(
      onDismissRequest = { showRingtoneDialog = false },
      title = { Text("Pilih Nada Dering Alarm") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          ringtones.forEach { tone ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  viewModel.updateNotificationSettings(ringtone = tone)
                  showRingtoneDialog = false
                  triggerDeviceVibration()
                }
                .padding(vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = notificationSettings.ringtone == tone,
                onClick = {
                  viewModel.updateNotificationSettings(ringtone = tone)
                  showRingtoneDialog = false
                  triggerDeviceVibration()
                }
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(tone, style = MaterialTheme.typography.bodyMedium)
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showRingtoneDialog = false }) {
          Text("Tutup")
        }
      }
    )
  }

  // Submit Modal
  showSubmitModal?.let { task ->
    AlertDialog(
      onDismissRequest = { showSubmitModal = null },
      icon = {
        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
      },
      title = { Text("Kumpulkan Tugas") },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Text("Membuka portal pengumpulan untuk '${task.title}' (${task.subject}).")
          if (task.notes.isNotBlank()) {
            Text(
              text = "Catatan Pengumpulan:\n${task.notes}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
          Text(
            text = "Tandai tugas ini sudah selesai setelah mengunggah berkas.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.toggleTaskCompletion(task)
            showSubmitModal = null
          }
        ) {
          Text("Tandai Selesai")
        }
      },
      dismissButton = {
        TextButton(onClick = { showSubmitModal = null }) {
          Text("Tutup")
        }
      }
    )
  }
}

@Composable
private fun AlarmRuleItem(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconTint: Color,
  isChecked: Boolean,
  onCheckedChange: (Boolean) -> Unit
) {
  Surface(
    shape = RoundedCornerShape(10.dp),
    color = MaterialTheme.colorScheme.surfaceContainerLow,
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onCheckedChange(!isChecked) }
  ) {
    Row(
      modifier = Modifier.padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.weight(1f)
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = iconTint,
          modifier = Modifier.size(20.dp)
        )
        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Checkbox(
        checked = isChecked,
        onCheckedChange = onCheckedChange,
        colors = CheckboxDefaults.colors(
          checkedColor = MaterialTheme.colorScheme.primary
        )
      )
    }
  }
}
