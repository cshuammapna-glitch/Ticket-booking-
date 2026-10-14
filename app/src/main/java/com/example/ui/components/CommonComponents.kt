package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenTertiary
import com.example.ui.theme.NavyPrimary

@Composable
fun StatusBadge(status: String, modifier: Modifier = Modifier) {
  val (bgColor, textColor, label, icon) = when (status) {
    "CONFIRMED" -> Quad(
      Color(0xFFE8F5E9),
      Color(0xFF2E7D32),
      "Ticket Confirmed (CNF)",
      Icons.Default.CheckCircle
    )
    "IN_PROGRESS" -> Quad(
      Color(0xFFE3F2FD),
      Color(0xFF1565C0),
      "Booking in Progress",
      Icons.Default.Sync
    )
    "WAITING_WINDOW" -> Quad(
      Color(0xFFFFF8E1),
      Color(0xFFF57F17),
      "Awaiting Tatkal Window",
      Icons.Default.HourglassTop
    )
    "PAYMENT_VERIFIED" -> Quad(
      Color(0xFFEDE7F6),
      Color(0xFF512DA8),
      "Payment Verified",
      Icons.Default.CheckCircle
    )
    "CANCELLED" -> Quad(
      Color(0xFFFFEBEE),
      Color(0xFFC62828),
      "Cancelled / Refunded",
      Icons.Default.AccessTime
    )
    else -> Quad(
      Color(0xFFF5F5F5),
      Color(0xFF616161),
      status,
      Icons.Default.AccessTime
    )
  }

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = bgColor,
    modifier = modifier.testTag("status_badge_$status")
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = textColor,
        modifier = Modifier.size(14.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = label,
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun TatkalTimerCard(
  title: String,
  timeWindow: String,
  countdown: String,
  isAc: Boolean,
  modifier: Modifier = Modifier
) {
  val cardBg = if (isAc) NavyPrimary else AmberSecondary

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = cardBg),
    modifier = modifier.testTag("tatkal_timer_card_${if (isAc) "ac" else "sleeper"}")
  ) {
    Column(
      modifier = Modifier.padding(14.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .clip(CircleShape)
              .background(Color(0xFF00E676))
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
        Surface(
          color = Color.White.copy(alpha = 0.2f),
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = timeWindow,
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Opens In:",
        color = Color.White.copy(alpha = 0.8f),
        fontSize = 11.sp
      )

      Text(
        text = countdown,
        color = Color.White,
        fontSize = 22.sp,
        fontWeight = FontWeight.ExtraBold,
        fontFamily = FontFamily.Monospace,
        letterSpacing = 1.sp
      )
    }
  }
}

@Composable
fun SectionHeader(title: String, subtitle: String? = null, action: @Composable (() -> Unit)? = null) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween,
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp)
  ) {
    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
      if (subtitle != null) {
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
    if (action != null) {
      action()
    }
  }
}
