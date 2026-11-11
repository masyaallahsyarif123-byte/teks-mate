package com.example

import com.example.data.model.TaskEntity
import com.example.data.model.TaskPriority
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun testTaskEntityCreation() {
    val task = TaskEntity(
      id = 1L,
      title = "Proyek Pemrograman Web",
      subject = "Pemrograman Web",
      dueDate = "2026-11-08",
      dueTime = "23:59",
      dueEpochMillis = 1794163140000L,
      priority = TaskPriority.TINGGI,
      alarmEnabled = true,
      isCompleted = false
    )

    assertEquals("Proyek Pemrograman Web", task.title)
    assertEquals(TaskPriority.TINGGI, task.priority)
    assertFalse(task.isCompleted)
    assertTrue(task.alarmEnabled)
  }
}
