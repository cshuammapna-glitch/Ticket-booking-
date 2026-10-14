package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.BookingRequestEntity
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenTertiary
import com.example.ui.theme.NavyContainer
import com.example.ui.theme.NavyPrimary
import com.example.viewmodel.BookingViewModel

@Composable
fun AdminAgentScreen(
  viewModel: BookingViewModel,
  onNavigateToDetail: (BookingRequestEntity) -> Unit
) {
  val bookings by viewModel.allBookings.collectAsStateWithLifecycle()
  val agentConfig by viewModel.agentConfig.collectAsStateWithLifecycle()
  val context = LocalContext.current

  var selectedTab by remember { mutableStateOf(0) } // 0: All, 1: Pending Fulfill, 2: Confirmed
  var bookingToFulfill by remember { mutableStateOf<BookingRequestEntity?>(null) }
  var showSettingsDialog by remember { mutableStateOf(false) }

  val filteredBookings = when (selectedTab) {
    1 -> bookings.filter { !it.isConfirmed }
    2 -> bookings.filter { it.isConfirmed }
    else -> bookings
  }

  val pendingCount = bookings.count { !it.isConfirmed }
  val confirmedCount = bookings.count { it.isConfirmed }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header & Control Desk Badge
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = NavyPrimary,
            modifier = Modifier.size(36.dp)
          ) {
            Icon(
              imageVector = Icons.Default.SupportAgent,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.padding(8.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "Agent Control Desk (Ham)",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 17.sp,
              color = MaterialTheme.colorScheme.onBackground
            )
            Text(
              text = "Receiver UPI: ${agentConfig.upiId}",
              fontSize = 11.sp,
              color = GreenTertiary,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        IconButton(onClick = { showSettingsDialog = true }) {
          Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = NavyPrimary)
        }
      }
    }

    // Stats Overview Row
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        AgentStatBox(
          title = "Total Orders",
          count = "${bookings.size}",
          color = NavyPrimary,
          modifier = Modifier.weight(1f)
        )
        AgentStatBox(
          title = "Pending Fulfill",
          count = "$pendingCount",
          color = AmberSecondary,
          modifier = Modifier.weight(1f)
        )
        AgentStatBox(
          title = "Tickets Issued",
          count = "$confirmedCount",
          color = GreenTertiary,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Agent Notice explaining fulfillment
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = NavyContainer.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = "Yahan Sabhi Passenger Ke Details & Aadhaar/PAN Aate Hain:",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = NavyPrimary
          )
          Text(
            text = "1. Customer details aur payment check karein.\n2. Tatkal booking time (10 AM / 11 AM) par IRCTC par ticket book karein.\n3. 'Issue Confirmed Ticket' par click karke PNR aur Seat number daal kar submit karein. Passenger ko turant E-Ticket mil jayega!",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 15.sp
          )
        }
      }
    }

    // Filter Tabs
    item {
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.clip(RoundedCornerShape(12.dp))
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = { Text("All (${bookings.size})", fontSize = 12.sp) }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = { Text("Pending ($pendingCount)", fontSize = 12.sp, color = if (pendingCount > 0) AmberSecondary else Color.Unspecified) }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = { Text("Confirmed ($confirmedCount)", fontSize = 12.sp) }
        )
      }
    }

    // Bookings List for Agent
    if (filteredBookings.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        ) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxWidth().padding(30.dp)
          ) {
            Text(
              text = "No booking requests in this category.",
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 13.sp
            )
          }
        }
      }
    } else {
      items(filteredBookings) { b ->
        AgentBookingItemCard(
          booking = b,
          onFulfill = { bookingToFulfill = b },
          onViewDetail = { onNavigateToDetail(b) },
          onDelete = {
            viewModel.agentDeleteBooking(b.id)
            Toast.makeText(context, "Deleted request ${b.bookingRef}", Toast.LENGTH_SHORT).show()
          }
        )
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  // Dialog: Fulfill & Issue Ticket with PNR & Seat numbers
  if (bookingToFulfill != null) {
    val b = bookingToFulfill!!
    var pnrInput by remember { mutableStateOf(b.pnrNumber.ifBlank { "245-${(1000000..9999999).random()}" }) }
    var coachInput by remember { mutableStateOf(b.coachNumber.ifBlank { "B3" }) }
    var seatInput by remember { mutableStateOf(b.seatBerth.ifBlank { "32 LB, 33 MB (CNF)" }) }
    var noteInput by remember { mutableStateOf(b.bookingTimeNote.ifBlank { "Booked at IRCTC Tatkal opening. Confirmed E-Ticket delivered." }) }

    AlertDialog(
      onDismissRequest = { bookingToFulfill = null },
      title = {
        Text("Issue Confirmed Ticket to Passenger", fontWeight = FontWeight.Bold, fontSize = 16.sp)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text(
            text = "Booking: ${b.bookingRef} • ${b.trainNumberOrName}",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          OutlinedTextField(
            value = pnrInput,
            onValueChange = { pnrInput = it },
            label = { Text("10-Digit IRCTC PNR Number") },
            placeholder = { Text("e.g. 245-8910294") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("admin_pnr_input")
          )

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = coachInput,
              onValueChange = { coachInput = it },
              label = { Text("Coach") },
              placeholder = { Text("B3") },
              singleLine = true,
              modifier = Modifier.weight(1f).testTag("admin_coach_input")
            )

            OutlinedTextField(
              value = seatInput,
              onValueChange = { seatInput = it },
              label = { Text("Seats / Berths") },
              placeholder = { Text("32 LB, 33 MB") },
              singleLine = true,
              modifier = Modifier.weight(2f).testTag("admin_seats_input")
            )
          }

          OutlinedTextField(
            value = noteInput,
            onValueChange = { noteInput = it },
            label = { Text("Booking Note for Customer") },
            singleLine = false,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.agentConfirmTicket(
              bookingId = b.id,
              pnr = pnrInput,
              coach = coachInput,
              seatBerth = seatInput,
              note = noteInput
            )
            Toast.makeText(context, "Ticket Issued! PNR: $pnrInput", Toast.LENGTH_LONG).show()
            bookingToFulfill = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = GreenTertiary),
          modifier = Modifier.testTag("admin_confirm_issue_button")
        ) {
          Text("Issue & Deliver Ticket")
        }
      },
      dismissButton = {
        TextButton(onClick = { bookingToFulfill = null }) {
          Text("Cancel")
        }
      }
    )
  }

  // Dialog: Settings (UPI ID & Concierge Fee)
  if (showSettingsDialog) {
    var upiInput by remember { mutableStateOf(agentConfig.upiId) }
    var phoneInput by remember { mutableStateOf(agentConfig.agentPhone) }
    var tatkalFeeInput by remember { mutableStateOf(agentConfig.tatkalServiceFee.toInt().toString()) }
    var generalFeeInput by remember { mutableStateOf(agentConfig.generalServiceFee.toInt().toString()) }

    AlertDialog(
      onDismissRequest = { showSettingsDialog = false },
      title = { Text("Agent Booking & Pricing Settings", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedTextField(
            value = upiInput,
            onValueChange = { upiInput = it },
            label = { Text("Receiver UPI ID (Where payment lands)") },
            placeholder = { Text("9709803741@mbk") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = phoneInput,
            onValueChange = { phoneInput = it },
            label = { Text("Helpline / WhatsApp Number") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = tatkalFeeInput,
            onValueChange = { tatkalFeeInput = it },
            label = { Text("Tatkal Concierge Fee (₹)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = generalFeeInput,
            onValueChange = { generalFeeInput = it },
            label = { Text("General Ticket Fee (₹)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            viewModel.updateAgentConfig(
              upi = upiInput,
              phone = phoneInput,
              tatkalFee = tatkalFeeInput.toDoubleOrNull() ?: 250.0,
              generalFee = generalFeeInput.toDoubleOrNull() ?: 120.0
            )
            Toast.makeText(context, "Settings updated!", Toast.LENGTH_SHORT).show()
            showSettingsDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
        ) {
          Text("Save Settings")
        }
      },
      dismissButton = {
        TextButton(onClick = { showSettingsDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun AgentStatBox(title: String, count: String, color: Color, modifier: Modifier = Modifier) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
  ) {
    Column(
      modifier = Modifier.padding(12.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = count, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = color)
    }
  }
}

@Composable
fun AgentBookingItemCard(
  booking: BookingRequestEntity,
  onFulfill: () -> Unit,
  onViewDetail: () -> Unit,
  onDelete: () -> Unit
) {
  val passengers = booking.passengerList

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          Text(
            text = "Ref: ${booking.bookingRef}",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = NavyPrimary
          )
          Text(
            text = "${booking.journeyDate} • ${booking.quota}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        StatusBadge(status = booking.status)
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "${booking.trainNumberOrName} (${booking.travelClass})",
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )
      Text(
        text = "${booking.fromStation} → ${booking.toStation}",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(8.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
      Spacer(modifier = Modifier.height(8.dp))

      // Passenger Aadhaar & PAN details submitted by user!
      Text(
        text = "Passenger & Identity Submissions:",
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurface
      )

      passengers.forEachIndexed { i, p ->
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
        ) {
          Column(modifier = Modifier.padding(8.dp)) {
            Text(
              text = "${i + 1}. ${p.fullName} (${p.age} Yrs / ${p.gender}) • Mob: ${p.mobileNumber}",
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              Text(
                text = "Aadhaar: ${p.formattedAadhaar}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = AmberSecondary
              )
              Text(
                text = "PAN: ${p.panNumber}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = NavyPrimary
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Payment Details
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Total Paid: ₹${booking.totalPaid.toInt()} on ${booking.upiId}",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color(0xFF2E7D32)
          )
          Text(
            text = "UTR: ${booking.utrNumber}",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontFamily = FontFamily.Monospace
          )
        }

        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
          Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action Buttons
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedButton(
          onClick = onViewDetail,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.weight(1f)
        ) {
          Text("View E-Ticket", fontSize = 12.sp)
        }

        Button(
          onClick = onFulfill,
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (booking.isConfirmed) NavyPrimary else GreenTertiary
          ),
          modifier = Modifier.weight(1.3f).testTag("fulfill_ticket_button_${booking.bookingRef}")
        ) {
          Icon(
            imageVector = if (booking.isConfirmed) Icons.Default.Edit else Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (booking.isConfirmed) "Update PNR" else "Issue Ticket",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
