package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsRailway
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.BookingRequestEntity
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.components.TatkalTimerCard
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenTertiary
import com.example.ui.theme.NavyContainer
import com.example.ui.theme.NavyPrimary
import com.example.viewmodel.BookingViewModel

@Composable
fun HomeScreen(
  viewModel: BookingViewModel,
  onNavigateToNewBooking: () -> Unit,
  onNavigateToBookingDetail: (BookingRequestEntity) -> Unit,
  onNavigateToAdminDesk: () -> Unit,
  onNavigateToSavedPassengers: () -> Unit,
  onNavigateToGuide: () -> Unit
) {
  val bookings by viewModel.allBookings.collectAsStateWithLifecycle()
  val agentConfig by viewModel.agentConfig.collectAsStateWithLifecycle()
  val acCountdown by viewModel.acCountdown.collectAsStateWithLifecycle()
  val sleeperCountdown by viewModel.sleeperCountdown.collectAsStateWithLifecycle()
  val context = LocalContext.current

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Hero Header Card
    item {
      Spacer(modifier = Modifier.height(4.dp))
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("hero_banner_card")
      ) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.linearGradient(
                colors = listOf(NavyPrimary, Color(0xFF1E3A8A), Color(0xFF0F172A))
              )
            )
            .padding(20.dp)
        ) {
          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = AmberSecondary,
                  modifier = Modifier.size(36.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.DirectionsRailway,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                      .padding(8.dp)
                      .size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "TatkalSeva",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                  )
                  Text(
                    text = "Ticket Booking Concierge",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.15f),
                modifier = Modifier.clickable { onNavigateToAdminDesk() }
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = "Agent Desk",
                    tint = AmberSecondary,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = "Agent Desk",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
              text = "Apna Detail & Payment Dein,\nHum Ticket Book Karke Denge!",
              color = Color.White,
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "Fill Aadhaar, PAN & Journey details. Make UPI payment to ${agentConfig.upiId}. Our booking agent books your confirmed ticket right when Tatkal opens & delivers in hours.",
              color = Color.White.copy(alpha = 0.85f),
              fontSize = 13.sp,
              lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
              onClick = {
                viewModel.resetWizard()
                onNavigateToNewBooking()
              },
              colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("book_ticket_cta_button")
            ) {
              Icon(
                imageVector = Icons.Default.FlashOn,
                contentDescription = null,
                tint = Color.White
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Book Tatkal / Train Ticket Now",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }
          }
        }
      }
    }

    // 2. Live Tatkal Countdown Clocks
    item {
      SectionHeader(
        title = "Live Tatkal Booking Windows",
        subtitle = "IRCTC opening countdown for tomorrow's journey"
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        TatkalTimerCard(
          title = "AC Tatkal",
          timeWindow = "10:00 AM",
          countdown = acCountdown,
          isAc = true,
          modifier = Modifier.weight(1f)
        )
        TatkalTimerCard(
          title = "Sleeper Tatkal",
          timeWindow = "11:00 AM",
          countdown = sleeperCountdown,
          isAc = false,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // 3. Process Explainer (3 Simple Steps)
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Kaise Kaam Karta Hai? (3 Steps)",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(12.dp))

          StepItem(
            step = "1",
            title = "Fill Passenger Aadhaar, PAN & Train",
            desc = "Enter passenger names, 12-digit Aadhaar, PAN, birth dates and preferred train/bus."
          )
          Spacer(modifier = Modifier.height(10.dp))
          StepItem(
            step = "2",
            title = "Pay Upfront via UPI (${agentConfig.upiId})",
            desc = "Scan QR code or pay to official UPI. Ticket fare + assistance fee is deposited securely."
          )
          Spacer(modifier = Modifier.height(10.dp))
          StepItem(
            step = "3",
            title = "We Book & Deliver Confirmed E-Ticket",
            desc = "When Tatkal/booking window opens, our executive books it fast & sends PNR with seats within hours!"
          )
        }
      }
    }

    // 4. Quick Services Row
    item {
      SectionHeader(title = "Our Concierge Services")
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        QuickServiceChip(
          icon = Icons.Default.FlashOn,
          label = "Tatkal Express",
          color = AmberSecondary,
          modifier = Modifier.weight(1f),
          onClick = {
            viewModel.resetWizard()
            viewModel.quota.value = "Tatkal"
            onNavigateToNewBooking()
          }
        )
        QuickServiceChip(
          icon = Icons.Default.DirectionsRailway,
          label = "General Train",
          color = NavyPrimary,
          modifier = Modifier.weight(1f),
          onClick = {
            viewModel.resetWizard()
            viewModel.quota.value = "General"
            onNavigateToNewBooking()
          }
        )
        QuickServiceChip(
          icon = Icons.Default.DirectionsBus,
          label = "AC Sleeper Bus",
          color = GreenTertiary,
          modifier = Modifier.weight(1f),
          onClick = {
            viewModel.resetWizard()
            viewModel.travelType.value = "Bus"
            onNavigateToNewBooking()
          }
        )
      }
    }

    // 5. Active & Recent Bookings
    item {
      SectionHeader(
        title = "My Booking Requests & Tickets",
        subtitle = "Real-time fulfillment tracking"
      )
    }

    if (bookings.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ConfirmationNumber,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "No Bookings Yet",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Click below to place your first Tatkal or express train booking request.",
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 13.sp,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }
    } else {
      items(bookings) { booking ->
        BookingSummaryCard(
          booking = booking,
          onClick = { onNavigateToBookingDetail(booking) }
        )
      }
    }

    // 6. Security and Guarantee Badge
    item {
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = NavyContainer.copy(alpha = 0.5f),
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 6.dp)
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(14.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = NavyPrimary,
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = "100% Verified Booking & ID Safety",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = NavyPrimary
            )
            Text(
              text = "Your Aadhaar, PAN & travel credentials are encrypted. Full refund guarantee if ticket could not be booked in Tatkal.",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom bar
    }
  }
}

@Composable
fun StepItem(step: String, title: String, desc: String) {
  Row(verticalAlignment = Alignment.Top) {
    Surface(
      shape = CircleShape,
      color = NavyPrimary,
      modifier = Modifier.size(24.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Text(
          text = step,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp
        )
      }
    }
    Spacer(modifier = Modifier.width(10.dp))
    Column {
      Text(
        text = title,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = desc,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 16.sp
      )
    }
  }
}

@Composable
fun QuickServiceChip(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  color: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Surface(
    shape = RoundedCornerShape(12.dp),
    color = MaterialTheme.colorScheme.surface,
    tonalElevation = 1.dp,
    modifier = modifier
      .clickable { onClick() }
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = label,
        tint = color,
        modifier = Modifier.size(22.dp)
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = label,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 1
      )
    }
  }
}

@Composable
fun BookingSummaryCard(
  booking: BookingRequestEntity,
  onClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("booking_card_${booking.bookingRef}")
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (booking.isConfirmed) Color(0xFFE8F5E9) else Color(0xFFE3F2FD),
            modifier = Modifier.padding(end = 8.dp)
          ) {
            Icon(
              imageVector = if (booking.travelType == "Bus") Icons.Default.DirectionsBus else Icons.Default.Train,
              contentDescription = null,
              tint = if (booking.isConfirmed) Color(0xFF2E7D32) else NavyPrimary,
              modifier = Modifier
                .padding(6.dp)
                .size(18.dp)
            )
          }
          Column {
            Text(
              text = booking.trainNumberOrName,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = "Ref: ${booking.bookingRef}",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        StatusBadge(status = booking.status)
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "From",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = booking.fromStation,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            maxLines = 1
          )
        }

        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowForward,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.outline,
          modifier = Modifier.padding(horizontal = 8.dp).size(16.dp)
        )

        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
          Text(
            text = "To",
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = booking.toStation,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            maxLines = 1
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          Text(
            text = "Date: ${booking.journeyDate} • ${booking.travelClass}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          if (booking.isConfirmed && booking.pnrNumber.isNotBlank()) {
            Text(
              text = "PNR: ${booking.pnrNumber} | Seat: ${booking.coachNumber}-${booking.seatBerth}",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = Color(0xFF2E7D32)
            )
          } else {
            Text(
              text = "Paid: ₹${booking.totalPaid.toInt()} via UPI",
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        Text(
          text = if (booking.isConfirmed) "View E-Ticket >" else "Track Status >",
          color = if (booking.isConfirmed) Color(0xFF2E7D32) else NavyPrimary,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp
        )
      }
    }
  }
}
