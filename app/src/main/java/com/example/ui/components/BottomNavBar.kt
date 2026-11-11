package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AppTab

@Composable
fun TaskMateBottomNav(
  currentTab: AppTab,
  onTabSelected: (AppTab) -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .navigationBarsPadding()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    contentAlignment = Alignment.Center
  ) {
    Surface(
      shape = CircleShape,
      color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.95f),
      shadowElevation = 8.dp,
      tonalElevation = 4.dp,
      modifier = Modifier
        .widthIn(max = 440.dp)
        .fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 6.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        NavItem(
          tab = AppTab.BERANDA,
          icon = Icons.Default.Dashboard,
          label = "Beranda",
          isSelected = currentTab == AppTab.BERANDA,
          onClick = { onTabSelected(AppTab.BERANDA) }
        )
        NavItem(
          tab = AppTab.TAMBAH,
          icon = Icons.Default.AddCircle,
          label = "Tambah",
          isSelected = currentTab == AppTab.TAMBAH,
          onClick = { onTabSelected(AppTab.TAMBAH) }
        )
        NavItem(
          tab = AppTab.DAFTAR,
          icon = Icons.Default.Checklist,
          label = "Daftar Tugas",
          isSelected = currentTab == AppTab.DAFTAR,
          onClick = { onTabSelected(AppTab.DAFTAR) }
        )
        NavItem(
          tab = AppTab.PENGINGAT,
          icon = Icons.Default.Alarm,
          label = "Pengingat",
          isSelected = currentTab == AppTab.PENGINGAT,
          onClick = { onTabSelected(AppTab.PENGINGAT) }
        )
      }
    }
  }
}

@Composable
private fun NavItem(
  tab: AppTab,
  icon: ImageVector,
  label: String,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val iconScale by animateFloatAsState(targetValue = if (isSelected) 1.12f else 1.0f, label = "iconScale")
  val contentColor by animateColorAsState(
    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
    label = "contentColor"
  )
  val backgroundColor by animateColorAsState(
    targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface.copy(alpha = 0f),
    label = "backgroundColor"
  )

  val interactionSource = remember { MutableInteractionSource() }

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center,
    modifier = Modifier
      .clip(CircleShape)
      .background(backgroundColor)
      .clickable(
        interactionSource = interactionSource,
        indication = null,
        onClick = onClick
      )
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .testTag("nav_${tab.name.lowercase()}")
  ) {
    Icon(
      imageVector = icon,
      contentDescription = label,
      tint = contentColor,
      modifier = Modifier
        .size(22.dp)
        .scale(iconScale)
    )
    Text(
      text = label,
      color = contentColor,
      style = MaterialTheme.typography.labelSmall.copy(
        fontSize = 10.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
      ),
      modifier = Modifier.padding(top = 2.dp)
    )
  }
}
