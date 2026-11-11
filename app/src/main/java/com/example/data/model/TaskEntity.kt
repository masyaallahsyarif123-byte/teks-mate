package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TaskPriority(val label: String, val deadlineDescription: String) {
  TINGGI("Tinggi", "< 24 Jam"),
  SEDANG("Sedang", "1-3 Hari"),
  RENDAH("Rendah", "> 3 Hari")
}

@Entity(tableName = "tasks")
data class TaskEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val subject: String,
  val dueDate: String, // YYYY-MM-DD
  val dueTime: String, // HH:mm
  val dueEpochMillis: Long,
  val priority: TaskPriority,
  val notes: String = "",
  val alarmEnabled: Boolean = true,
  val isCompleted: Boolean = false,
  val createdAtEpochMillis: Long = System.currentTimeMillis()
)
