package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
  private val localeId = Locale("id", "ID")

  fun formatIndonesianDate(epochMillis: Long): String {
    val sdf = SimpleDateFormat("EEEE, d MMMM yyyy", localeId)
    return sdf.format(Date(epochMillis))
  }

  fun formatShortDate(dateStr: String): String {
    return try {
      val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
      val date = parser.parse(dateStr) ?: return dateStr
      val formatter = SimpleDateFormat("d MMM yyyy", localeId)
      formatter.format(date)
    } catch (e: Exception) {
      dateStr
    }
  }

  fun formatRelativeDeadline(dueEpochMillis: Long, nowEpochMillis: Long): String {
    val diffMillis = dueEpochMillis - nowEpochMillis
    val diffHours = diffMillis / (1000 * 60 * 60)
    val diffMinutes = (diffMillis / (1000 * 60)) % 60
    val diffDays = diffMillis / (1000 * 60 * 60 * 24)

    return when {
      diffMillis < 0 -> "Lewat batas"
      diffHours < 1 -> "Tersisa $diffMinutes menit!"
      diffHours < 24 -> "Tersisa ${diffHours} jam ${diffMinutes}m"
      diffDays == 1L -> "Besok"
      diffDays < 7L -> "Tersisa $diffDays hari"
      else -> "${diffDays} hari lagi"
    }
  }

  fun getRemainingTimeText(dueEpochMillis: Long, nowEpochMillis: Long): String {
    val diffMillis = dueEpochMillis - nowEpochMillis
    if (diffMillis <= 0) return "00 Jam : 00 Menit"
    val hours = diffMillis / (1000 * 60 * 60)
    val minutes = (diffMillis / (1000 * 60)) % 60
    return String.format(Locale.getDefault(), "%02d Jam : %02d Menit", hours, minutes)
  }

  fun isToday(dueEpochMillis: Long): Boolean {
    val cal1 = Calendar.getInstance()
    val cal2 = Calendar.getInstance().apply { timeInMillis = dueEpochMillis }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
        cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
  }

  fun isUrgent(dueEpochMillis: Long, nowEpochMillis: Long): Boolean {
    val diffMillis = dueEpochMillis - nowEpochMillis
    val diffHours = diffMillis / (1000 * 60 * 60)
    return diffHours in 0..24
  }
}
