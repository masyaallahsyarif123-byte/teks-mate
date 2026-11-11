package com.example

import com.example.data.model.TaskPriority
import com.example.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun testTaskPriorityLabels() {
    assertEquals("Tinggi", TaskPriority.TINGGI.label)
    assertEquals("Sedang", TaskPriority.SEDANG.label)
    assertEquals("Rendah", TaskPriority.RENDAH.label)
  }

  @Test
  fun testDateUtilsFormatShortDate() {
    val formatted = DateUtils.formatShortDate("2026-11-08")
    assertNotNull(formatted)
    assertTrue(formatted.contains("2026") || formatted.contains("Nov"))
  }

  @Test
  fun testDateUtilsRemainingTime() {
    val now = 1000000L
    val due = now + (2 * 60 * 60 * 1000L) + (30 * 60 * 1000L)
    val text = DateUtils.getRemainingTimeText(due, now)
    assertEquals("02 Jam : 30 Menit", text)
  }
}
